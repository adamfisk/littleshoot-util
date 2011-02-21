package org.littleshoot.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of the connection maintainer interface.
 * 
 * @param <T> The type of the object that identifies a server.
 * @param <ServerT> The type of the object that represents a connection to a 
 * server.
 */
public final class ConnectionMaintainerImpl<T, ServerT> implements
        ConnectionMaintainer<ServerT> {
    /**
     * The log for this class.
     */
    private final Logger LOG = LoggerFactory
            .getLogger(ConnectionMaintainerImpl.class);

    /**
     * Collection utilities.
     */
    private final CollectionUtils m_collectionUtils;

    /**
     * The object used to establish connections.
     */
    private final ConnectionEstablisher<T, ServerT> m_establisher;

    /**
     * The object that provides candidate server identifiers with which to try
     * establishing connections.
     */
    private final CandidateProvider<T> m_candidateProvider;

    /**
     * The list of locally known server identifiers to try.
     */
    private final List<T> m_serverIdsToTry;

    /**
     * The list of server identifiers that we are currently "using". We "use" a
     * server identifier when we are either connected to it or are trying to
     * connect to it.
     */
    private final List<T> m_usedServerIds;

    /**
     * The minimum number of servers with which to connect. If the number of
     * servers with which we are connected dips below this number, we try to
     * connect to more servers.
     */
    private final int m_minNumConnected;

    /**
     * The mapping of server identifiers to the actual servers to which the
     * connection was made.
     */
    private final Map<T, ServerT> m_idMap;

    /**
     * The thread that kicks off connections when necessary.
     */
    private final Thread m_thread;

    /**
     * The number of servers with which we are connected.
     */
    private int m_numConnected;

    /**
     * The number of outstanding connection attempts.
     */
    private int m_outstanding;

    /**
     * The most recently active server. This can be null before any activity has
     * been detected.
     */
    private Object m_mostRecentlyActiveId;

    /**
     * Whether or not to sleep before trying to get more candidate servers.
     */
    private boolean m_sleepBeforeTryingToGetMoreCandidates;

    /**
     * We perform slightly differently on the first run, not sleeping before
     * trying the server, for example.
     */
    private volatile boolean m_firstRun = true;

    /**
     * Constructs a new connection maintainer.
     * 
     * @param establisher
     *            The object used to establish connections.
     * @param candidateProvider
     *            The object that provides candidate servers with which to try
     *            establishing connections.
     * @param minNumConnected
     *            The minimum number of connections to maintain.
     */
    public ConnectionMaintainerImpl(
            final ConnectionEstablisher<T, ServerT> establisher,
            final CandidateProvider<T> candidateProvider,
            final int minNumConnected) {
        m_collectionUtils = new CollectionUtilsImpl();
        m_establisher = establisher;
        m_candidateProvider = candidateProvider;

        m_serverIdsToTry = new LinkedList<T>();
        m_usedServerIds = new LinkedList<T>();
        m_minNumConnected = minNumConnected;
        m_idMap = new HashMap<T, ServerT>();

        final Runnable runner = new ConnectionRunner();
        final String threadName = getClass().getSimpleName() + "-"
                + runner.hashCode() + "-establisher-"
                + establisher.getClass().getSimpleName();

        m_thread = new Thread(runner, threadName);
        m_thread.setDaemon(true);

        m_numConnected = 0;
        m_outstanding = 0;

        m_sleepBeforeTryingToGetMoreCandidates = false;
        m_mostRecentlyActiveId = null;
        m_firstRun = true;
    }

    /**
     * A predicate that evaluates whether a server is unused by this connection
     * maintainer.
     */
    private class UnusedServer implements Predicate<T> {
        public boolean evaluate(final T object) {
            return (!isUsed(object));
        }
    }

    /**
     * The runnable object that is run by the thread that maintains connections.
     */
    private class ConnectionRunner implements Runnable {
        public void run() {
            while (true) {
                synchronized (ConnectionMaintainerImpl.this) {
                    while (!getShouldTryAnother()) {
                        LOG.debug("Waiting to try another");
                        ThreadUtils.safeWait(ConnectionMaintainerImpl.this);
                    }
                }

                try {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Trying to connect...");
                    }
                    tryConnecting();
                } catch (final RuntimeException runtimeException) {
                    // We catch exceptions here to make sure that the thread
                    // that maintains connections does not get killed by
                    // uncaught exceptions.

                    // This could be a Spring runtime exception connecting
                    // to Hessian, for example.
                    LOG.warn("Exception while trying to connect",
                            runtimeException);
                }
            }
        }
    }

    /**
     * The listener to be notified of changes to connection status.
     */
    private class MyListener implements ConnectionMaintainerListener<ServerT> {
        /**
         * The server identifier for which we are listening for connection
         * events.
         */
        private final T m_serverId;

        /**
         * Constructs a new listener.
         * 
         * @param serverId
         *            The server for which we are listening for connection
         *            events.
         */
        private MyListener(final T serverId) {
            m_serverId = serverId;
        }

        public void connected(final ServerT server) {
            LOG.debug("Got connected: " + m_serverId);
            synchronized (ConnectionMaintainerImpl.this) {
                --m_outstanding;
                ++m_numConnected;
                m_idMap.put(m_serverId, server);

                ConnectionMaintainerImpl.this.notify();
            }

            m_mostRecentlyActiveId = m_serverId;
        }

        public void reconnected() {
            m_mostRecentlyActiveId = m_serverId;
        }

        public void connectionFailed() {
            LOG.debug("Got connectionFailed: " + m_serverId);

            synchronized (ConnectionMaintainerImpl.this) {
                --m_outstanding;
                ConnectionMaintainerImpl.this.notify();

                // We stop using this server.
                m_usedServerIds.remove(m_serverId);

                ConnectionMaintainerImpl.this.notify();
            }
        }

        /**
         * {@inheritDoc}
         */
        public void disconnected() {
            LOG.debug("Got disconnected: " + m_serverId);

            synchronized (ConnectionMaintainerImpl.this) {
                --m_numConnected;

                // Make sure that we had previously connected to the server.

                m_idMap.remove(m_serverId);

                // We stop using this server.
                m_usedServerIds.remove(m_serverId);

                ConnectionMaintainerImpl.this.notify();
            }
        }
    }

    /**
     * Returns whether a given server is already in use.
     * 
     * @param candidate
     *            The URI of the server to check.
     * 
     * @return Whether a given server is already in use.
     */
    private synchronized boolean isUsed(final T candidate) {
        LOG.debug("Checking if used: " + candidate);

        LOG.debug("Used? " + m_usedServerIds.contains(candidate));

        return (m_usedServerIds.contains(candidate));
    }

    /**
     * Returns a server identifier to try.
     * 
     * @return A server identifier to try. If we are unable to come up with any
     *         candidates, a <code>None</code> object is returned.
     */
    private Optional<T> getServerIdToTry() {
        if (m_serverIdsToTry.isEmpty()) {
            final Collection<T> moreCandidates = getMoreCandidates();

            if (moreCandidates.isEmpty()) {
                // We were unable to find more candidates. To avoid hammering
                // the service that provides potential candidates, flag that we
                // should sleep for a little while before trying again to get
                // more potential candidates.
                m_sleepBeforeTryingToGetMoreCandidates = true;

                return (new NoneImpl<T>());
            } else {
                // We successfully retrieved more candidates. We clear the
                // flag that prevented requesting more potential candidates,
                // since all is well. Since we have candidates with which to
                // work, we probably will not be hitting the service that
                // provides potential candidates any time soon.
                m_sleepBeforeTryingToGetMoreCandidates = false;

                m_serverIdsToTry.addAll(moreCandidates);

                return (new SomeImpl<T>(m_serverIdsToTry.remove(0)));
            }
        } else {
            // We already have servers to try. Try one of them.
            return (new SomeImpl<T>(m_serverIdsToTry.remove(0)));
        }
    }

    /**
     * Returns a collection of more candidates that can be tried for
     * connections.
     * 
     * @return A collection of more candidates that can be tried for
     *         connections.
     */
    private Collection<T> getMoreCandidates() {
        final Predicate<T> unusedPredicate = new UnusedServer();
        final Collection<T> servers = new LinkedList<T>();

        // We limit the number of times we try to retrieve more candidates. If
        // we try several times and fail, we just return an empty collection.
        int tries = 0;

        // We pull candidate URIs from the candidate provider.
        while (servers.isEmpty() && (tries < 3)) {
            LOG.debug("Trying to find candidate servers");

            // If we have been flagged to sleep before trying to get more
            // candidates, sleep before trying to get more candidates.
            if (m_sleepBeforeTryingToGetMoreCandidates) {
                LOG.debug("Long sleep...");
                ThreadUtils.safeSleep(60000);
            } else if (!m_firstRun) {
                LOG.debug("Short sleep...");
                // Always sleep for a little while to avoid hammering the
                // server.
                ThreadUtils.safeSleep(10000);
            }

            m_firstRun = false;

            final Collection<T> candidates = m_candidateProvider
                    .getCandidates();

            LOG.debug("Found candidates...");
            // We only accept servers that we have not already used. This
            // prevents us from establishing connections with the same
            // servers more than once.
            final Collection<T> unusedCandidates = m_collectionUtils.select(
                    candidates, unusedPredicate);

            servers.addAll(unusedCandidates);

            ++tries;
        }

        LOG.debug("Found this many candidate servers: " + servers.size());

        return servers;
    }

    /**
     * Establishes a connection to a given server, making sure to update all of
     * the bookkeeping associated with doing so.
     * 
     * @param serverId
     *            The identifier of the server to which to connect.
     */
    private void establish(final T serverId) {
        LOG.debug("Connecting to: " + serverId);

        // We create a listener that will be notified of activity on this
        // connection.
        final ConnectionMaintainerListener<ServerT> listener = new MyListener(
                serverId);

        synchronized (this) {
            // Until we see a response from this attempt to establish a
            // connection, we have an outstanding connection attempt.
            ++m_outstanding;

            // We also track the fact that we have "used" the server.
            m_usedServerIds.add(serverId);
        }

        m_establisher.establish(serverId, listener);
    }

    /**
     * Handle the case when we have not yet met our connectoin quota, and there
     * are no candidates to try.
     */
    /*
     * We currently do not use a last resort, since we should always be able to
     * get candidates. -jjc
     * 
     * private void handleNoServerIdToTry () { final Object lastResortServerId =
     * m_candidateProvider.getLastResort ();
     * 
     * // Try and establish a connection with the last resort server if we //
     * are not already trying. if (!isUsed (lastResortServerId)) { LOG.debug
     * ("Trying last resort: " + lastResortServerId);
     * 
     * establish (lastResortServerId); } }
     */

    /**
     * Returns whether we should try another connection given the current
     * status.
     * 
     * @return Whether we should try another connection given the current
     *         status.
     */
    private synchronized boolean getShouldTryAnother() {
        return ((m_outstanding + m_numConnected) < m_minNumConnected);
    }

    /**
     * Tries connecting to a server. This method is called when we need to try
     * to establish a connection to a server.
     */
    private void tryConnecting() {
        LOG.debug("Trying to connect");

        // Get a server with which to try to connect.
        final Optional<T> optionalServerIdToTry = getServerIdToTry();

        // It is possible that we are unable to find a server. In this case,
        // fall back to the last resort server.
        final OptionalVisitor<Void, T> serverToTryVisitor = new OptionalVisitor<Void, T>() {
            public Void visitNone(final None<T> none) {
                LOG.debug("No connections to try...");
                // We could not find a server. Handle this case appropriately.

                // We only need this if we start running a naked server
                // somewhere to support our network. Otherwise, any running
                // servers should be found by the candidate provider.
                // handleNoServerIdToTry ();
                return null;
            }

            public Void visitSome(final Some<T> some) {
                // We found a server. Establish a connection with it.
                LOG.debug("Establishing connection...");
                establish(some.object());
                return null;
            }
        };

        optionalServerIdToTry.accept(serverToTryVisitor);
    }

    public void start() {
        // All we have to start the connection maintenance process is start the
        // thread that handles all registration. If we have already started
        // the thread, do nothing.
        if (!m_thread.isAlive()) {
            m_thread.start();
        } else {
            LOG.warn("We should not be trying to register multiple times");
        }
    }

    public synchronized Optional<ServerT> getMostRecentlyActive() {
        if (m_idMap.containsKey(m_mostRecentlyActiveId)) {
            return new SomeImpl<ServerT>(m_idMap.get(m_mostRecentlyActiveId));
        } else {
            return new NoneImpl<ServerT>();
        }
    }

    public synchronized Collection<ServerT> getConnectedServers() {
        LOG.debug("m_idMap.size: " + m_idMap.size());
        return Collections.unmodifiableCollection(m_idMap.values());
    }
}
