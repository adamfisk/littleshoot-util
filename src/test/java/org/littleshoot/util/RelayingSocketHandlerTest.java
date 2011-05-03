package org.littleshoot.util;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelayingSocketHandlerTest {
    
    private static final int PORT = 8889;
    private static final int RELAY_PORT = 8899;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final InetSocketAddress serverAddress = 
        new InetSocketAddress("127.0.0.1", PORT);
    private SocketAddress relayServer = 
        new InetSocketAddress("127.0.0.1", RELAY_PORT); 

    @Test public void testRelay() throws Exception {
        startEchoServer();

        byte[] readKey = CommonUtils.generateKey();
        byte[] writeKey = CommonUtils.generateKey();
        
        startRelayServer(readKey, writeKey);
        Thread.yield();
        Thread.sleep(400);
        
        final Socket sock = new Socket();
        sock.connect(relayServer, 4000);
        final CipherSocket cipher = new CipherSocket(sock, writeKey, readKey);
        final OutputStream os = cipher.getOutputStream();
        final String msg = "what up my cracka?";
        log.info("Original message length: {}", msg.length());
        final byte[] msgBytes = msg.getBytes();
        os.write(msgBytes);
        
        final byte[] readBuf = new byte[100];
        final InputStream is = cipher.getInputStream();
        final int read = is.read(readBuf);
        assertEquals(msgBytes.length, read);
        System.out.println("READ "+read);
        final String received = new String(readBuf, 0, read);
        System.out.println("'"+msg+"'");
        System.out.println("'"+received+"'");
        assertEquals(msg, received);
    }

    private void startRelayServer(final byte[] readKey, final byte[] writeKey) 
        throws IOException {
        final ServerSocket server = new ServerSocket();
        server.bind(relayServer);
        final Runnable runner = new Runnable() {
            public void run() {
                try {
                    final Socket sock = server.accept();
                    final RelayingSocketHandler relay = 
                        new RelayingSocketHandler(serverAddress, writeKey, readKey);
                    log.info("Notifying relay of socket");
                    relay.onSocket("testing", sock);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        };
        final Thread t = new Thread(runner);
        t.setDaemon(true);
        t.start();
    }

    private void startEchoServer() throws IOException {
        final ServerSocket server = new ServerSocket();
        server.bind(serverAddress);
        final Runnable runner = new Runnable() {
            public void run() {
                try {
                    final Socket sock = server.accept();

                    final InputStream is = sock.getInputStream();
                    final byte[] plainText = new byte[1024*1024];
                    final int read = is.read(plainText);
                    log.info("Bytes read on echo server: {}", read);
                    final String originalString = 
                        new String(plainText, 0, read);
                    log.info("Got original string: {}", originalString);
                    final OutputStream os = sock.getOutputStream();
                    final byte[] bytes = originalString.getBytes();
                    log.info("Echoing array with length: {}", bytes.length);
                    os.write(bytes);
                    server.close();
              
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        };
        final Thread t = new Thread(runner);
        t.setDaemon(true);
        t.start();
    }
}
