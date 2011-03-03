package org.littleshoot.util;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import junit.framework.TestCase;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests the utility class for manipulating <code>ByteBuffer</code>s.
 */
public final class ByteBufferUtilTest extends TestCase {

    private final Logger LOG = LoggerFactory.getLogger(getClass());
    
    
    @Test
    public void testToArray() throws Exception {
        byte[] data = new byte[10];
        Arrays.fill(data, (byte)10);
        ByteBuffer[] bufs = ByteBufferUtils.toArray(data, 1000);
        assertEquals("Unexpected number of buffers!!", 1, bufs.length);
        assertEquals("Unexpected buf size", data.length, bufs[0].remaining());
        
        
        data = new byte[1001];
        Arrays.fill(data, (byte)1);
        bufs = ByteBufferUtils.toArray(data, 10);
        assertEquals("Unexpected number of buffers!!", 101, bufs.length);
        assertEquals("Unexpected buf size", 1, bufs[100].remaining());
        
        data = new byte[127];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) i;
        }
        bufs = ByteBufferUtils.toArray(data, 10);
        assertEquals("Unexpected number of buffers!!", 13, bufs.length);
        assertEquals("Unexpected buf size", 7, bufs[12].remaining());
        
        byte expected = 0;
        for (final ByteBuffer bb : bufs) {
            while (bb.hasRemaining()) {
                assertEquals("Unexpected byte in buffer", expected, bb.get());
                expected++;
            }
        }
    }
    
    /**
     * Tests the utility method for determining the number of remaining bytes in
     * a group of <code>ByteBuffer</code>s.
     * 
     * @throws Exception
     *             If any unexpected error occurs.
     */
    @Test
    public void testRemaining() throws Exception {
        final ByteBuffer buffer0 = createBuffer(5);
        final ByteBuffer buffer1 = createBuffer(15);
        final ByteBuffer buffer2 = createBuffer(25);
        final ByteBuffer buffer3 = createBuffer(35);
        final Collection buffers = new LinkedList();
        buffers.add(buffer0);
        buffers.add(buffer1);
        buffers.add(buffer2);
        buffers.add(buffer3);

        assertEquals(80, ByteBufferUtils.remaining(buffers));
    }

    /**
     * Tests the method for combining multiple buffers into one buffer.
     * 
     * @throws Exception
     *             If any unexpected error occurs.
     */
    @Test
    public void testCombine() throws Exception {
        final ByteBuffer buffer0 = createBuffer(5);
        final ByteBuffer buffer1 = createBuffer(15);
        final ByteBuffer buffer2 = createBuffer(25);
        final ByteBuffer buffer3 = createBuffer(35);
        final Collection buffers = new LinkedList();
        buffers.add(buffer0);
        buffers.add(buffer1);
        buffers.add(buffer2);
        buffers.add(buffer3);

        final ByteBuffer combined = ByteBufferUtils.combine(buffers);

        assertEquals(80, combined.remaining());

        for (int i = 0; i < 5; i++) {
            assertEquals(i, combined.get());
        }
        for (int i = 0; i < 15; i++) {
            assertEquals(i, combined.get());
        }
        for (int i = 0; i < 25; i++) {
            assertEquals(i, combined.get());
        }
        for (int i = 0; i < 35; i++) {
            assertEquals(i, combined.get());
        }
    }

    @Test
    private ByteBuffer createBuffer(final int length) {
        final ByteBuffer buf = ByteBuffer.allocate(length);
        for (int i = 0; i < length; i++) {
            buf.put((byte) i);
        }
        buf.rewind();
        return buf;
    }

    /**
     * Tests the method for splitting a large buffer into multiple smaller
     * buffers.
     * 
     * @throws Exception
     *             If any unexpected error occurs.
     */
    @Test
    public void testSplit() throws Exception {
        final int chunkSize = 1000;
        final int numChunks = 10;

        // We add the 5 at the end to give us an extra set of tricky bytes
        // to handle correctly.
        final int limit = chunkSize * numChunks + 5;
        final ByteBuffer bigBuffer = ByteBuffer.allocate(limit);
        for (int i = 0; i < limit; i++) {
            bigBuffer.put((byte) (i % 255));
        }

        bigBuffer.flip();

        final ByteBuffer testBuffer = bigBuffer.duplicate();

        final Collection buffers = ByteBufferUtils.split(bigBuffer, chunkSize);
        assertEquals(numChunks + 1, buffers.size());

        int totalLength = 0;
        for (final Iterator iter = buffers.iterator(); iter.hasNext();) {
            LOG.trace("Testing next buffer...");
            final ByteBuffer curBuffer = (ByteBuffer) iter.next();

            while (curBuffer.hasRemaining()) {
                final byte testByte = testBuffer.get();
                assertEquals(testByte, curBuffer.get());
                totalLength++;
            }
        }

        // Make sure the total size of the buffers is the size we expect.
        assertEquals(limit, totalLength);
    }
}
