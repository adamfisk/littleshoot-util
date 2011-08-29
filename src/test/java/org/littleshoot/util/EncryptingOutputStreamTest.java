package org.littleshoot.util;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.junit.Test;

/**
 * Test for the encryption and decryption functions.
 */
public class EncryptingOutputStreamTest {
    
    @Test public void testEncodingTinyData() throws Exception {
        runTest(1);
    }
    
    public void testEncodingHugeData() throws Exception {
        runTest(20000);
    }
    
    @Test public void testEncodingNormalData() throws Exception {
        runTest(40);
    }
    
    public void runTest(final int size) throws Exception {
        final byte[] key = CommonUtils.generateKey();
        final byte[] data = buildData(size);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final EncryptingOutputStream os = new EncryptingOutputStream(key, baos);
        os.write(data);
        
        final byte[] encrypted = baos.toByteArray();
        final byte[] decrypted = CommonUtils.decode(key, encrypted);
        assertTrue("Decrypted not equal to plain text!!", 
            Arrays.equals(data, decrypted));
    }

    private byte[] buildData(final int size) {
        final String base = "ajfi";
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(base);
        }
        return sb.toString().getBytes();
    }
}
