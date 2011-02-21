package org.littleshoot.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Utility interface for any class that wants to customize handling of an
 * <code>InputStream</code>.
 */
public interface InputStreamHandler
    {

    /**
     * Handles the specified <code>InputStream</code>.  Implementors of this 
     * method MUST call close on the <code>InputStream</code> when they are
     * done processing it.
     * 
     * @param is The <code>InputStream</code> to handle.
     * @throws IOException If there's a read error reading from the stream or
     * an output error while handling it.
     */
    void handleInputStream(final InputStream is) throws IOException;
    }
