package org.littleshoot.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.net.ssl.SSLSocket;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Socket handler that simply relays data to a server socket.
 */
public class RelayingSocketHandler implements SessionSocketListener {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    /**
     * The default small buffer size to use.  This is smaller because HTTP
     * requests aren't typically that big.
     */
    private static final int SMALL_BUFFER_SIZE = 1024 * 4;
    
    /**
     * The default buffer size to use. This is the same size Jetty uses -- 
     * bigger because we're typically serving files.
     */
    private static final int LARGE_BUFFER_SIZE = 1024 * 16;

    private final InetSocketAddress serverAddress;

    private final byte[] readKey;

    private final byte[] writeKey;
 
    /**
     * Creates a new socket handler. Allows a custom port.
     * 
     * @param port The port to use for connecting locally.
     */
    /*
    public RelayingSocketHandler(final InetSocketAddress serverAddress) {
        this(serverAddress, null, null);
    }
    */
    
    /**
     * Creates a new socket handler. Allows a custom port.
     * 
     * @param port The port to use for connecting locally.
     */
    public RelayingSocketHandler(final InetSocketAddress serverAddress,
        final byte[] readKey, final byte[] writeKey) {
        this.serverAddress = serverAddress;
        this.readKey = readKey;
        this.writeKey = writeKey;
    }

    public void onSocket(final String id, final Socket encryptedSocket) 
        throws IOException {
        log.info("Relaying socket connecting to: {}", this.serverAddress);
        
        final Socket sock;
        if (encryptedSocket instanceof SSLSocket) {
            sock = encryptedSocket;
        } else if (readKey == null || writeKey == null) {
            // In this case the socket will not actually have encrypted data.
            sock = encryptedSocket;
        } else {
            log.info("Creating CipherSocket with write key {} and read key {}", 
                    writeKey, readKey);
            sock = new CipherSocket(encryptedSocket, writeKey, readKey);
        }
            
        final Socket relay = new Socket();
        relay.connect(this.serverAddress, 30 * 1000);

        // We set this relatively low because we're accessing a file from the
        // HTTP server and expect the server to be doing most of the sending.
        relay.setSoTimeout(30 * 1000);

        final OutputStream externalOs = sock.getOutputStream();
        final InputStream externalIs = sock.getInputStream();
        final OutputStream relayOs = relay.getOutputStream();
        final InputStream relayIs = relay.getInputStream();

        // Thread the reads and the writes. "Reads" and "writes" of course
        // depend on what connection you're taking the perspective of, but
        // it doesn't really matter.
        threadedCopy(externalIs, relayOs, "ReadFromExternal", 
            SMALL_BUFFER_SIZE, sock, this.readKey);
        threadedCopy(relayIs, externalOs, "WriteToExternal", 
            LARGE_BUFFER_SIZE, sock, this.writeKey);
    }

    private void threadedCopy(final InputStream is, final OutputStream os,
        final String threadNameId, final int bufferSize, final Socket sock, 
        final byte[] key) {
        final Runnable runner = new Runnable() {
            public void run() {
                try {
                    copyLarge(is, os, bufferSize);
                } catch (final IOException e) {
                    // This will happen if the other side just closes the
                    // socket, for example.
                    log.debug("Error copying socket data on "+threadNameId, e);
                } catch (final Throwable t) {
                    log.warn("Error copying socket data on " + threadNameId, t);
                } finally {
                    // Flush to be sure we've written everything.
                    try {
                        os.flush();
                    } catch (final IOException e1) {
                    }
                    // Just close everything.
                    IOUtils.closeQuietly(os);
                    IOUtils.closeQuietly(is);
                    log.warn("Closing socket...already closed streams...");
                    try {
                        sock.close();
                    } catch (final IOException e) {
                    }
                }
            }
        };
        final Thread thread = new Thread(runner,
                "RelayingSocketHandler-Thread-" + threadNameId + "-"
                        + runner.hashCode());
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * This is copied from Jakarta Commons IOUtils.  For some reason our series
     * of atypical socket stream chains break without a flush after each of 
     * these calls, so it's added in.
     * 
     * Copy bytes from a large (over 2GB) <code>InputStream</code> to an
     * <code>OutputStream</code>.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * 
     * @param input  the <code>InputStream</code> to read from
     * @param output  the <code>OutputStream</code> to write to
     * @param bufferSize The size of the buffer to use.
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException if an I/O error occurs
     * @since Commons IO 1.3
     */
    private long copyLarge(final InputStream input, final OutputStream output,
            final int bufferSize) throws IOException {
        final byte[] buffer = new byte[bufferSize];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        log.debug("Copied bytes: {}", count);
        return count;
    }
}
