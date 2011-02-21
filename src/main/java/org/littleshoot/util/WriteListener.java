package org.littleshoot.util;

import java.util.EventListener;

/**
 * Utility class for listening to write events.
 */
public interface WriteListener extends EventListener
    {

    /**
     * Called when new bytes are read.
     * @param bytesRead The number of new bytes read.
     */
    void onBytesRead(final int bytesRead);

    }
