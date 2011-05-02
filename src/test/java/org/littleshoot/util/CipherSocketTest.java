package org.littleshoot.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;
import org.littleshoot.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CipherSocketTest {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private static final String MSG = createMessage();
    
    private final AtomicReference<String> serverMessage = 
        new AtomicReference<String>();
    
    public void testCipher() throws Exception {
    }
    
    @Test public void testCipherSockets() throws Exception {
        final String msg = createMessage();
        
        // Get the KeyGenerator
        final byte[] writeKey = CommonUtils.generateKey();
        final byte[] readKey = CommonUtils.generateKey();
        final byte[] data = msg.getBytes();
        
        startServer(writeKey, readKey);
        final Socket plainClient = new Socket("127.0.0.1", 8889);
        final Socket client = new CipherSocket(plainClient, writeKey, readKey);
        final OutputStream os = client.getOutputStream();
        os.write(data);
        os.flush();
        os.close();
        
        int waits = 0;
        while (serverMessage.get() == null && waits < 20) {
            Thread.sleep(400);
            waits++;
        }
        final String server = serverMessage.get();
        if (server.equalsIgnoreCase("error")) {
            fail("Got an error!!");
        }
        else {
            log.info(MSG);
            log.info(server);
            assertEquals(MSG, server);
        }
    }

    private static String createMessage() {
        final String hello = "HelloWorld-";
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append(hello);
        }
        return sb.toString();
    }

    private void startServer(
        final byte[] readKey, final byte[] writeKey) throws IOException {
        final ServerSocket server = new ServerSocket();
        server.bind(new InetSocketAddress("127.0.0.1", 8889));
        final Runnable runner = new Runnable() {
            public void run() {
                try {
                    final Socket plainSock = server.accept();
                    final Socket sock = 
                        new CipherSocket(plainSock, writeKey, readKey);
                    final InputStream is = sock.getInputStream();
                    final byte[] ciphertext = new byte[1024*1024];
                    final int read = is.read(ciphertext);
                    final String originalString = 
                        new String(ciphertext, 0, read);
                    log.info("Got original string: {}", originalString);
                    serverMessage.set(originalString);
                    server.close();
              
                } catch (final Exception e) {
                    e.printStackTrace();
                    serverMessage.set("ERROR");
                }
            }
        };
        final Thread t = new Thread(runner);
        t.setDaemon(true);
        t.start();
    }
}
