package org.littleshoot.util;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DecryptingInputStreamTest {

    private static final Logger log = 
        LoggerFactory.getLogger(DecryptingInputStreamTest.class);
    
    @Test public void testRead() throws Exception {
        final String original = "helloooo world";
        final byte[] readKey = CommonUtils.generateKey();
        final byte[] buf = buildBuf(original, readKey);
        final ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        
        final DecryptingInputStream is = new DecryptingInputStream(readKey, bais);
        final byte[] readBuf = new byte[20];
        final int read = is.read(readBuf, 0, readBuf.length);
        
        final String decoded = new String(readBuf, 0, read);
        assertEquals(original, decoded);
    }
    
    @Test public void testReallyLongLengthRead() throws Exception {
        final String original = longMessage("helloooo world");
        final byte[] readKey = CommonUtils.generateKey();
        final byte[] buf = buildBuf(original, readKey);
        final ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        
        final DecryptingInputStream is = 
            new DecryptingInputStream(readKey, bais);
        final byte[] readBuf = new byte[1024 * 16];
        final int read = is.read(readBuf, 0, readBuf.length);
        
        final String decoded = new String(readBuf, 0, read);
        assertEquals(original, decoded);
    }

    @Test public void testMultipleMessages() throws Exception {
        final String original1 = longMessage();
        final String original2 = longMessage("-why hello there-");
        final String original = original1 + original2;
        final byte[] readKey = CommonUtils.generateKey();
        final byte[] buf1 = buildBuf(original1, readKey);
        final byte[] buf2 = buildBuf(original2, readKey);
        final byte[] buf = CommonUtils.combine(buf1, buf2);
        final ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        
        final DecryptingInputStream is = new DecryptingInputStream(readKey, bais);
        int index = 0;
        final int chunkSize = 20;
        final byte[] readBuf = new byte[chunkSize];
        final StringBuilder sb = new StringBuilder();
        log.info("About to read");
        int i = 0;
        final int originalLength = original.getBytes().length;
        log.info("Original length: {}", originalLength);
        while (index < originalLength) {
            final int read = is.read(readBuf, 0, readBuf.length);
            if (read == -1) {
                break;
            }
            final String newString = new String(readBuf, 0, read);
            log.info("Appending string: {}", newString);
            sb.append(newString);
            index += read;
            i++;
        }
        
        final String decoded = sb.toString();
        assertEquals("Not equal:\n"+original+"\n"+decoded+"\n", original, decoded);
    }
    
    @Test public void testLongDataRead() throws Exception {
        final String original = longMessage();
        final byte[] readKey = CommonUtils.generateKey();
        final byte[] buf = buildBuf(original, readKey);
        final ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        
        final DecryptingInputStream is = new DecryptingInputStream(readKey, bais);
        int index = 0;
        final int chunkSize = 20;
        final byte[] readBuf = new byte[chunkSize];
        final StringBuilder sb = new StringBuilder();
        int i = 0;
        while (index < original.getBytes().length) {
            final int read = is.read(readBuf, 0, readBuf.length);
            sb.append(new String(readBuf, 0, read).trim());
            index += read;
            i++;
        }
        
        final String decoded = sb.toString();
        //System.out.println("read: "+new String(readBuf));
        assertEquals("Not equal:\n"+original+"\n"+decoded+"\n", original, decoded);
    }

    private String longMessage() {
        return longMessage("helloooo world-");
    }
    
    private String longMessage(final String original) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(original);
        }
        return sb.toString();
    }

    private byte[] buildBuf(final String original, final byte[] key) {
        
        final byte[] data = original.getBytes();
        final byte[] msg = CommonUtils.encode(key, data, 0, data.length);
        final byte[] mac = new byte[32];
        System.arraycopy(msg, msg.length-32, mac, 0, 32);
        return msg;
    }
}
