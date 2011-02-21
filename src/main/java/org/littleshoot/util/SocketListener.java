package org.littleshoot.util;

import java.io.IOException;
import java.net.Socket;

/**
 * General interface for classes that handle sockets.
 */
public interface SocketListener
    {

    /**
     * Tells the listener to handle an already configured socket.
     * 
     * @throws IOException If there's an IO error reading the socket data or
     * writing a response.
     */
    void onSocket(Socket sock) throws IOException;
    

    }
