package org.littleshoot.util;

import java.io.IOException;
import java.net.Socket;

/**
 * General interface for classes that handle sockets.
 */
public interface SocketListener {

    /**
     * Notifies the listener of the socket.
     * 
     * @param sock The {@link Socket}.
     * @throws IOException If there's an IO error reading the socket data or
     * writing a response.
     */
    void onSocket(Socket sock) throws IOException;
}
