package org.littleshoot.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link InputStream} that decodes incoming data that's encrypted with the
 * given key and that includes a SHA-256 MAC.
 */
public class DecryptingInputStream extends InputStream {

    private static final Logger LOG = 
        LoggerFactory.getLogger(DecryptingInputStream.class);
    
    private final byte[] oneByte = new byte[1];

    private final byte[] readKey;

    private final InputStream inputStream;
    
    private ByteBuffer lastBuffer;
    
    private InputRecord currentRecord;

    /**
     * Creates a new stream that decrypts data from the encapsulated stream.
     * 
     * @param readKey The key to decrypt with.
     * @param inputStream The wrapped {@link InputStream}.
     */
    public DecryptingInputStream(final byte[] readKey, 
        final InputStream inputStream) {
        this.readKey = readKey;
        this.inputStream = inputStream;
        currentRecord = new InputRecord(readKey);
    }

    /**
     * Return the minimum number of bytes that can be read without blocking.
     * Currently not synchronized.
     */
    @Override
    public int available() throws IOException {
        return this.inputStream.available();
    }

    @Override
    public synchronized int read(final byte buf[]) throws IOException {
        return read(buf, 0, buf.length);
    }
    
    /**
     * Read up to "len" bytes into this buffer, starting at "off".
     * If the layer above needs more data, it asks for more, so we
     * are responsible only for blocking to fill at most one buffer,
     * and returning "-1" on non-fault EOF status.
     */
    @Override
    public synchronized int read(final byte buf[], final int off, final int len)
            throws IOException {
        while (this.currentRecord.needsData()) {
            final byte[] data = new byte[len];
            final int read = this.inputStream.read(data);
            if (read == -1) {
                return read;
            }
            lastBuffer = ByteBuffer.wrap(data, 0, read);
            this.currentRecord.addData(lastBuffer);
        }
        final int bytesRead = this.currentRecord.drainData(buf, off, len);
        if (!this.currentRecord.hasMoreData()) {
            LOG.info("Resetting app record");
            this.currentRecord = new InputRecord(readKey);
            if (lastBuffer != null) {
                this.currentRecord.addData(lastBuffer);
            } else {
                LOG.warn("No existing buffer?");
            }
        }
        return bytesRead;
    }

    @Override
    public synchronized int read() throws IOException {
        int n = read(oneByte, 0, 1);
        if (n <= 0) { // EOF
            return -1;
        }
        return oneByte[0] & 0xff;
    }

    @Override
    public void close() throws IOException {
        this.inputStream.close();
    }

    @Override
    public String toString() {
        return "DecryptingInputStream [inputStream=" + inputStream + "]";
    }
    
    // inherit default mark/reset behavior (throw Exceptions) from InputStream

}
