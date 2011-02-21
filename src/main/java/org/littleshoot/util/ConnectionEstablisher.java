package org.littleshoot.util;

/**
 * The interface to an object responsible for establishing connections to
 * servers.
 * 
 * @param <T>
 *      The type of the object that identifies a server.
 * @param <ServerT>
 *      The type of the object that represents a connection to a server.
 */
public interface ConnectionEstablisher<T,ServerT>
    {
    /**
     * Establishes a connection to a given server.
     *
     * @param serverId
     *      The identifier of the server.
     * @param listener
     *      The listener to be notified of connection events.
     */
    void establish
            (T serverId,
             ConnectionMaintainerListener<ServerT> listener);
    }
