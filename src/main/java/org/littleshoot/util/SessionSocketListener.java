package org.littleshoot.util;

import java.io.IOException;
import java.net.Socket;

/**
 * General interface for classes that handle sockets with IDs.
 */
public interface SessionSocketListener {
    
    /**
     * Notifies the listener of the socket along with the associated ID.
     * 
     * @param id The ID for the socket.
     * @param sock The {@link Socket}.
     * @throws IOException If there's an IO error reading the socket data or
     * writing a response.
     */
    void onSocket(String id, Socket sock) throws IOException;
    

}
