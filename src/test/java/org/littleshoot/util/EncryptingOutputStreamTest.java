package org.littleshoot.util;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.junit.Test;

/**
 * Test for the encryption and decryption functions.
 */
public class EncryptingOutputStreamTest {

    @Test public void testEncoding() throws Exception {
        final byte[] key = CommonUtils.generateKey();
        final byte[] data = buildData();

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final EncryptingOutputStream os = new EncryptingOutputStream(key, baos);
        os.write(data);
        
        final byte[] encrypted = baos.toByteArray();
        final byte[] decrypted = CommonUtils.decode(key, encrypted);
        assertTrue("Decrypted not equal to plain text!!", 
            Arrays.equals(data, decrypted));
    }

    private byte[] buildData() {
        final String base = "geterdone";
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            sb.append(base);
        }
        return sb.toString().getBytes();
    }
}
