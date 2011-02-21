package org.littleshoot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Write listener that does nothing on write events.
 */
public final class NoOpWriteListener implements WriteListener
    {

    /**
     * Logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(NoOpWriteListener.class);
    
    public void onBytesRead(final int bytesRead)
        {
        // Does nothing.
        if (LOG.isDebugEnabled())
            {
            LOG.debug("read "+bytesRead+ " bytes...");
            }
        }

    }
