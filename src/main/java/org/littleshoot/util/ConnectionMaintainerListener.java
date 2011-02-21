package org.littleshoot.util;

/**
 * A listener that is notified of changes to connection status given by a
 * connection maintainer.
 * 
 * @param <T>
 *      The type of a server object to which we may be notified of connection
 *      status.
 */
public interface ConnectionMaintainerListener<T>
    {
    /**
     * Called on a successful connection.
     *
     * @param server
     *      The server to which we connected.
     */
    void connected
            (T server);

    /**
     * Called on a successful reconnection.
     */
    void reconnected
            ();

    /**
     * Called on a failed attempt at connection.
     */
    void connectionFailed
            ();

    /**
     * Called when we are disconnected.
     */
    void disconnected
            ();
    }
