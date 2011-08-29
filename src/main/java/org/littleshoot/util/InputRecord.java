package org.littleshoot.util;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encapsulates a single record of encrypted input data.
 */
public class InputRecord {

    private static final Logger log = 
        LoggerFactory.getLogger(InputRecord.class);
    
    private static final int MAC_SIZE = 32;

    private int size = -1;

    private final byte[] readKey;

    private byte[] headerBytes = new byte[3];
    
    private final ByteBuffer header = ByteBuffer.wrap(headerBytes);
    
    private ByteBuffer curBuffer = header;

    private byte[] bodyBytes;

    private boolean needsData = true;

    private byte[] plainText;
    
    private int drainedIndex = 0;
    
    /**
     * Creates a new input record that will decode data using the specified
     * key.
     * 
     * @param readKey The key to decode data with.
     */
    public InputRecord(final byte[] readKey) {
        if (readKey == null) {
            log.error("Read key can't be null!!");
            throw new NullPointerException("Null read key");
        }
        this.readKey = readKey;
    }

    public boolean needsData() {
        return this.needsData;
    }

    public void addData(final ByteBuffer bb) {
        if (curBuffer == header) {
            copyBytes(curBuffer, bb);
            if (curBuffer.hasRemaining()) {
                return;
            } else {
                this.size = CommonUtils.unsignedShortToInt(
                    new byte[]{this.headerBytes[1], this.headerBytes[2]});
                this.bodyBytes = new byte[size + MAC_SIZE];
                curBuffer = ByteBuffer.wrap(bodyBytes);
                copyBytes(curBuffer, bb);
                if (curBuffer.hasRemaining()) {
                    return;
                } else {
                    decryptAndVerify(curBuffer);
                }
            }
        } else {
            copyBytes(curBuffer, bb);
            if (curBuffer.hasRemaining()) {
                return;
            } else {
                decryptAndVerify(curBuffer);
            }
        }
    }
    

    private void decryptAndVerify(final ByteBuffer bodyAndMac) {
        
        final byte[] bodyAndMacBytes = bodyAndMac.array();
        final byte[] cipherText = new byte[size];
        
        System.arraycopy(bodyAndMacBytes, 0, cipherText, 0, cipherText.length);
        
        final byte[] rawMac = new byte[32];
        System.arraycopy(bodyAndMacBytes, cipherText.length, rawMac, 0, rawMac.length);
        
        final SecretKeySpec skeySpec = new SecretKeySpec(readKey, "AES");
        final Cipher cipher;
        final byte[] plain;
        try {
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            //cipher.update(cipherText, off, len);
            //plainText = cipher.doFinal();
            plain = cipher.doFinal(cipherText);
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No AES?", e);
        } catch (final NoSuchPaddingException e) {
            throw new IllegalArgumentException("No padding?", e);
        } catch (final InvalidKeyException e) {
            throw new IllegalArgumentException("Bad key? Read key is: "+
                CommonUtils.toHex(readKey), e);
        } catch (final IllegalBlockSizeException e) {
            throw new IllegalArgumentException("Bad block size?", e);
        } catch (final BadPaddingException e) {
            throw new IllegalArgumentException("Bad padding?", e);
        }

        // Does the mac include the length and the version? Probably.
        final Mac mac256;
        try {
            mac256 = Mac.getInstance("hmacSHA256");
            mac256.init(skeySpec);
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No hmacSHA256?", e);
        } catch (final InvalidKeyException e) {
            throw new IllegalArgumentException("Bad key?", e);
        }

        mac256.update(headerBytes);
        mac256.update(cipherText);
        final byte[] mac = mac256.doFinal();

        // Now make sure the MACs match.
        if (!Arrays.equals(mac, rawMac)) {
            log.error("MACs don't match!!");
            log.error("Decrypted: "+new String(plain));
            log.error("Tried to match original:\n"+
                CommonUtils.toHex(rawMac)+"\n"+CommonUtils.toHex(mac));
            throw new IllegalArgumentException("Macs don't match!!");
        }
        this.plainText = plain;
        this.needsData = false;
    }
    
    public int drainData(final byte[] buf, final int off, final int len) {
        final int toCopy;
        final int remaining = this.plainText.length - drainedIndex;
        if (remaining > len) {
            toCopy = len;
        } else {
            toCopy = remaining;
        }
        System.arraycopy(this.plainText, drainedIndex, buf, off, toCopy);
        drainedIndex += toCopy;
        return toCopy;
    }

    public boolean hasMoreData() {
        final int remaining = this.plainText.length - drainedIndex;
        return remaining > 0;
    }

    private void copyBytes(ByteBuffer buf1, ByteBuffer buf2) {
        while (buf1.hasRemaining() && buf2.hasRemaining()) {
            buf1.put(buf2.get());
        }
    }

}
