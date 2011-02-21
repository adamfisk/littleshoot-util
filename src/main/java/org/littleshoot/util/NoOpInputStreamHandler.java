package org.littleshoot.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Class that does nothing when passed an {@link InputStream} to process.
 */
public class NoOpInputStreamHandler implements InputStreamHandler
    {

    public void handleInputStream(final InputStream is) throws IOException
        {
        // Ignore.
        }

    }
