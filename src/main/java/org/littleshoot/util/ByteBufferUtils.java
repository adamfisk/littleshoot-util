package org.littleshoot.util;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for manipulating <code>ByteBuffer</code>s.
 */
public final class ByteBufferUtils
    {
    
    /**
     * Logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ByteBufferUtils.class);

    /**
     * Splits the specified <code>ByteBuffer</code> into smaller 
     * <code>ByteBuffer</code>s of the specified size.  The remaining bytes 
     * in the buffer must be greater than the chunk size.  This method will
     * create smaller buffers of the specified size until there are fewer 
     * remaining bytes than the chunk size, when it will simply add a buffer
     * the same size as the number of bytes remaining.
     * 
     * @param buffer The <code>ByteBuffer</code> to split.
     * @param chunkSize The size of the smaller buffers to create.
     * @return A <code>Collection</code> of <code>ByteBuffer</code>s of the
     * specified size.  The final buffer in the <code>Collection</code> will
     * have a size > 0 and <= the chunk size.
     */
    public static Collection<ByteBuffer> split(final ByteBuffer buffer, 
        final int chunkSize)
        {
        final Collection<ByteBuffer> buffers = new LinkedList<ByteBuffer>();
        final int limit = buffer.limit();
        
        int totalSent = 0;
        while ((totalSent + chunkSize) < limit)
            {
            LOG.trace("Setting limit to: "+(totalSent + chunkSize));
            buffer.limit(totalSent + chunkSize);
            buffers.add(createBuffer(buffer));            
            totalSent += chunkSize;
            }
        
        // Send any remaining bytes.
        buffer.limit(limit);
        buffers.add(createBuffer(buffer));
        return buffers;
        }
    
    private static ByteBuffer createBuffer(final ByteBuffer buffer)
        {
        final ByteBuffer data = ByteBuffer.allocate(
            buffer.limit() - buffer.position());
        
        LOG.trace("Created buffer with capacity: "+data.capacity());
        data.put(buffer);
        data.rewind();
        return data;
        }

    /**
     * Combines the remaining data from the given <code>Collection</code> of
     * <code>ByteBuffer</code>s into a single consolidated 
     * <code>ByteBuffer</code>.
     * 
     * @param buffers The <code>Collection</code> of <code>ByteBuffer</code>s
     * to make into a single buffer.
     * @return A new <code>ByteBuffer</code> combining the remaining data of
     * the <code>Collection</code> of <code>ByteBuffer</code>s.
     */
    public static ByteBuffer combine(final Collection<ByteBuffer> buffers)
        {
        final ByteBuffer buf = ByteBuffer.allocate(remaining(buffers));
        for (final Iterator iter = buffers.iterator(); iter.hasNext();)
            {
            final ByteBuffer curBuf = (ByteBuffer) iter.next();
            buf.put(curBuf);
            }
        buf.flip();
        return buf;
        }
    
    public static int remaining(final Collection<ByteBuffer> buffers)
        {
        int remaining = 0;
        for (final Iterator iter = buffers.iterator(); iter.hasNext();)
            {
            final ByteBuffer buf = (ByteBuffer) iter.next();
            remaining += buf.remaining();
            }
        return remaining;
        }
    
    /**
     * Logs the data contained in a <code>ByteBuffer</code> that's ready
     * to be written to the network.
     * 
     * @param buffer The buffer to log.
     */
    public static void logBufferToWrite(final ByteBuffer buffer)
        {
        LOG.trace("Writing: ");
        LOG.trace(toString(buffer));
        }

    /**
     * Returns the buffer as a string while preserving the buffer position
     * and limit.
     * 
     * @param buffer The buffer to create a string from.
     * @return The buffer string.
     */
    public static String toString(final ByteBuffer buffer)
        {
        final int position = buffer.position();
        final int limit = buffer.limit();
        final byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        
        final String dataString = new String(data);
        
        buffer.position(position);
        buffer.limit(limit);
        
        return dataString;
        }

    public static byte[] readBytes16(final ByteBuffer buf)
        {
        final int length = Unsigned.getUnsignedShort(buf);
        final byte[] bytes = new byte[length];
        try
            {
            buf.get(bytes);
            }
        catch (final BufferUnderflowException e)
            {
            LOG.error("Underflow. Expected "+length+" bytes", e);
            throw e;
            }
        return bytes;
        }
    
    private static final byte[] highDigits;

    private static final byte[] lowDigits;

    // initialize lookup tables
    static 
        {
        final byte[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F' };

        int i;
        final byte[] high = new byte[256];
        final byte[] low = new byte[256];

        for (i = 0; i < 256; i++) 
            {
            high[i] = digits[i >>> 4];
            low[i] = digits[i & 0x0F];
            }

        highDigits = high;
        lowDigits = low;
        }

    /**
     * Utility method for dumping a buffer in hex.
     * 
     * @param in The buffer to dump.
     * @return The hex string.
     */
    public static String getHexdump(final ByteBuffer in) 
        {
        int size = in.remaining();

        if (size == 0) 
            {
            return "ByteBufferUtils::getHexdump buffer is empty";
            }

        final StringBuilder out = new StringBuilder((in.remaining() * 3) - 1);

        final int mark = in.position();

        // fill the first
        int byteValue = in.get() & 0xFF;
        out.append((char) highDigits[byteValue]);
        out.append((char) lowDigits[byteValue]);
        size--;

        // and the others, too
        for (; size > 0; size--) {
            out.append(' ');
            byteValue = in.get() & 0xFF;
            out.append((char) highDigits[byteValue]);
            out.append((char) lowDigits[byteValue]);
        }

        in.position(mark);

        return out.toString();
        }

    public static byte[] toRawBytes(final ByteBuffer buf)
        {
        final int mark = buf.position();
        final byte[] bytes = new byte[buf.remaining()];
        buf.get(bytes);
        
        buf.position(mark);
        return bytes;
        }
    }
