package org.littleshoot.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class that writes data from an <code>InputStream</code> to 
 * a <code>File</code>.
 */
public class FileInputStreamHandler implements InputStreamHandler
    {

    /**
     * Logger for this class.
     */
    private final Logger LOG = LoggerFactory.getLogger(FileInputStreamHandler.class);
 
    /**
     * The <code>File</code> on disk to write to.
     */
    private final File m_file;

    /**
     * The listener for write events.
     */
    private WriteListener m_writeListener;

    /**
     * Creates a new stream handler that will write the 
     * <code>InputStream</code> to the specified <code>File</code>.
     * 
     * @param file The <code>File</code> instance denoting the file path
     * to write to.
     */
    public FileInputStreamHandler(final File file)
        {
        this(file, new NoOpWriteListener());
        }
    
    /**
     * Creates a new stream handler that will write the 
     * <code>InputStream</code> to the specified <code>File</code>.
     * @param file The <code>File</code> instance denoting the file path
     * to write to.
     * @param writeListener The listener for write events.
     */
    public FileInputStreamHandler(final File file, 
        final WriteListener writeListener)
        {
        LOG.trace("Creating stream handler for file: "+file);
        this.m_file = file;
        this.m_writeListener = writeListener;
        }

    /* (non-Javadoc)
     * @see com.bamboo.util.InputStreamHandler#handleInputStream(
     * java.io.InputStream)
     */
    public void handleInputStream(final InputStream is) throws IOException
        {
        LOG.trace("Handling input stream.");
        final OutputStream os = new FileOutputStream(this.m_file);
        try
            {
            final long bytesWritten = org.littleshoot.util.IoUtils.copy(is, os, 
                this.m_writeListener);
            LOG.trace("Wrote "+bytesWritten+" to file: "+this.m_file);
            }
        finally
            {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
            }
        }
    }
