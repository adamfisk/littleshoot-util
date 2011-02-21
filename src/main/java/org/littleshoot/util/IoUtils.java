package org.littleshoot.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.LongRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility functions for IO.
 */
public final class IoUtils
    {
    
    /**
     * Logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(IoUtils.class);
    
    /**
     * The default buffer size to use.
     */
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    
    /**
     * Copy bytes from an <code>InputStream</code> to an
     * <code>OutputStream</code>.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * 
     * @param is The <code>InputStream</code> to read from
     * @param os The <code>OutputStream</code> to write to
     * @param listener The listener for write events.
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is <code>null</code>
     * @throws IOException if an I/O error occurs 
     */
    public static int copy(final InputStream is, final OutputStream os, 
        final WriteListener listener) throws IOException
        {
        if (is == null)
            {
            throw new NullPointerException("null input stream.");
            }
        if (os == null)
            {
            throw new NullPointerException("null output stream.");
            }
        final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int count = 0;
        int n = 0;
        while (-1 != (n = is.read(buffer))) 
            {
            os.write(buffer, 0, n);
            listener.onBytesRead(n);
            count += n;
            }
        LOG.trace("Wrote "+count+" bytes.");
        return count;
        }
    
    /** 
     * Copies the {@link InputStream} to the specified {@link OutputStream}
     * for the specified number of bytes or until EOF or exception.
     * 
     * @param in The {@link InputStream} to copy from. 
     * @param out The {@link OutputStream} to copy to.
     * @param originalByteCount The number of bytes to copy.
     * @return The number of bytes written.
     * @throws IOException If there's an IO error copying the bytes.
     */
    public static long copy(final InputStream in, final OutputStream out,
        final long originalByteCount) throws IOException 
        {
        if (originalByteCount < 0)
            {
            throw new IllegalArgumentException("Invalid byte count: " + 
                    originalByteCount);
            }
        final byte buffer[] = new byte[DEFAULT_BUFFER_SIZE];
        int len = 0;
        long written = 0;
        long byteCount = originalByteCount;
        try
            {
            while (byteCount > 0)
                {
                //len = in.read(buffer);
                if (byteCount < DEFAULT_BUFFER_SIZE)
                    {
                    len = in.read(buffer, 0, (int)byteCount);
                    }
                else
                    {
                    len = in.read(buffer, 0, DEFAULT_BUFFER_SIZE);
                    }

                if (len == -1)
                    {
                    LOG.debug("Breaking on length = -1");
                    //System.out.println("Breaking on -1");
                    break;
                    }

                byteCount -= len;
                LOG.info("Total written: "+written);
                out.write(buffer, 0, len);
                written += len;
                //LOG.debug("IoUtils now written: "+written);
                }
            //System.out.println("Out of while: "+byteCount);
            return written;
            }
        catch (final IOException e)
            {
            LOG.debug("Got IOException during copy after writing "+
                written+" of "+originalByteCount, e);
            e.printStackTrace();
            throw e;
            }
        catch (final RuntimeException e)
            {
            LOG.debug("Runtime error after writing "+
                written+" of "+originalByteCount, e);
            e.printStackTrace();
            throw e;
            }
        finally
            {
            out.flush();
            }
        }

    /**
     * Copy method for copying data from an {@link InputStream} to a 
     * {@link RandomAccessFile}.
     * 
     * @param is The input data.
     * @param raf The file to write to.
     * @param offset The offset within the file to start writing at.
     * @param length The number of bytes to copy.
     * @return The number of bytes copied.
     * @throws IOException If any IO error occurs.
     */
    public static long copy(final InputStream is, 
        final RandomAccessFile raf, final long offset, long length) 
        throws IOException
        {
        return copy(is, raf, offset, length, null, null);
        }
    
    /**
     * Copy method for copying data from an {@link InputStream} to a 
     * {@link RandomAccessFile}.
     * 
     * @param is The input data.
     * @param raf The file to write to.
     * @param offset The offset within the file to start writing at.
     * @param length The number of bytes to copy.
     * @return The number of bytes copied.
     * @throws IOException If any IO error occurs.
     */
    public static long copy(final InputStream is, 
        final RandomAccessFile raf, final long offset, long length,
        final LongRangeListener lrl, final WriteListener wl) 
        throws IOException
        {
        if (length < 0)
            {
            throw new IllegalArgumentException("Invalid byte count: " + 
                length);
            }
        final byte buffer[] = new byte[DEFAULT_BUFFER_SIZE];
        int bytesRead = 0;
        long written = 0;
        long filePosition = offset;
        try
            {
            while (length > 0)
                {
                if (length < DEFAULT_BUFFER_SIZE)
                    {
                    bytesRead = is.read(buffer, 0, (int) length);
                    }
                else
                    {
                    bytesRead = is.read(buffer, 0, DEFAULT_BUFFER_SIZE);
                    }

                if (bytesRead == -1)
                    {
                    break;
                    }

                length -= bytesRead;
                synchronized (raf)
                    {
                    raf.seek(filePosition);
                    raf.write(buffer, 0, bytesRead);
                    }
                if (wl != null)
                    {
                    wl.onBytesRead(bytesRead);
                    }
                if (lrl != null)
                    {
                    lrl.onRangeComplete(new LongRange(filePosition, filePosition+bytesRead-1));
                    }
                filePosition += bytesRead;
                written += bytesRead;
                //LOG.debug("IoUtils now written: {}", written);
                }
            return written;
            }
        catch (final IOException e)
            {
            LOG.debug("Got IOException during copy", e);
            throw e;
            }
        catch (final RuntimeException e)
            {
            LOG.warn("Runtime error", e);
            throw e;
            }
        }

    public static byte[] deflate(final String str)
        {
        GZIPOutputStream os = null;
        ByteArrayOutputStream baos = null;
        byte[] raw = null;
        try
            {
            raw = str.getBytes("UTF-8");
            baos = new ByteArrayOutputStream();
            os = new GZIPOutputStream(baos);
            os.write(raw);
            os.finish();
            baos.close();
            return baos.toByteArray();
            }
        catch (final UnsupportedEncodingException e)
            {
            // Never.
            LOG.error("Encoding??", e);
            return raw;
            }
        catch (final IOException e)
            {
            LOG.error("Could not deflate!!", e);
            return raw;
            }
        finally
            {
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(baos);
            }
        }

    public static String inflateString(final byte[] bytes)
        {
        try
            {
            return new String(inflate(bytes), "UTF-8");
            }
        catch (final UnsupportedEncodingException e)
            {
            LOG.error("Encoding??", e);
            return new String(bytes);
            }
        }
    
    public static byte[] inflate(final byte[] bytes)
        {
        if (bytes == null)
            {
            throw new IllegalArgumentException("Null bytes argument");
            }
        if (bytes.length == 0)
            {
            LOG.warn("Received empty byte array!!");
            return bytes;
            }
        InputStream bais = null;
        InputStream is = null;
        try
            {
            bais = new ByteArrayInputStream(bytes);
            is = new GZIPInputStream(bais);
            return IOUtils.toByteArray(is);
            }
        catch (final UnsupportedEncodingException e)
            {
            // Never.
            LOG.error("Encoding??", e);
            return bytes;
            }
        catch (final IOException e)
            {
            try
                {
                LOG.error("Could not inflate: "+new String(bytes, "UTF-8"), e);
                }
            catch (final UnsupportedEncodingException e1)
                {
                // Never.
                LOG.error("Encoding??", e);
                }
            return bytes;
            }
        finally
            {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(bais);
            }
        } 
    }
