package org.littleshoot.util;

import java.util.Collection;

/**
 * The interface to an object that robustly maintains connections to a number
 * of servers.
 * 
 * @param <T>
 *      The type of the object that identifies a server.
 */
public interface ConnectionMaintainer<T>
    {
    /**
     * Starts this maintainer.
     */
    void start
            ();

    /**
     * Returns the most recently active server.
     *
     * @return
     *      The most recently active server.  If no server has been active yet,
     *      a <code>None</code> object is returned.  Otherwise, a
     *      <code>Some</code> object with map entry of the most recently active
     *      server is returned.  The type of the server is determined by the
     *      types used by the implementation.  In the default implementation,
     *      the type of the server is determined by the
     *      <code>ConnectionEstablisher</code> that is used.
     */
    public Optional<T> getMostRecentlyActive
            ();

    /**
     * Returns a collection of the servers that are currently connected.
     *
     * @return
     *      A collection of the servers that are currently connected.  The type
     *      of these servers is determined by the types used by the
     *      implementation.  In the default implementation, the type of the
     *      server is determined by the <code>ConnectionEstablisher</code> that
     *      is used.
     */
    public Collection<T> getConnectedServers
            ();
    }
