package org.littleshoot.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * General utilities specific to LittleShoot.
 */
public class CommonUtils {
    
    private static final Logger LOG = 
        LoggerFactory.getLogger(CommonUtils.class);
    
    private static final boolean IS_PRO = true;
    
    /**
     * The maximum number of search results for a single source for the 
     * free version.
     */
    public static final int FREE_RESULT_LIMIT = 40;

    public static final String LIMEWIRE_ENABLED_KEY = "LIMEWIRE_ENABLED";

    public static final String SEEDING_ENABLED_KEY = "SEEDING_ENABLED";

    public static final String UPLOAD_SPEED_KEY = "UPLOAD_SPEED";
    
    private final static KeyGenerator keyGenerator;
    
    static {
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No AES?", e);
        }
    }

    private CommonUtils() {}
    
    public static String toString(final byte[] bytes) {
        try {
            return new String(bytes, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            return "";
        }
    }
    
    public static void threadedCopy(final InputStream is, final OutputStream os,
        final String threadName) {
        final Runnable runner = new Runnable() {
            public void run() {
                try {
                    IOUtils.copy(is, os);
                } catch (final IOException e) {
                    LOG.info("Exception on copy. Hung up?", e);
                }
            }
        };
        final Thread t = new Thread(runner, threadName);
        t.setDaemon(true);
        t.start();
    }
    
    /**
     * Gets the directory to use for LittleShoot data.
     * 
     * @return The platform-specific LittleShoot data directory.
     */
    public static File getDataDir() {
        final File dir;
        if (SystemUtils.IS_OS_WINDOWS) {
            dir = new File(System.getenv("APPDATA"), "LittleShoot");
        } else if (SystemUtils.IS_OS_MAC_OSX) {
            // TODO: Is this correct on international machines??
            dir = new File("/Library/Application\\ Support/LittleShoot");
        } else {
            dir = getLittleShootDir();
        }

        if (dir.isDirectory() || dir.mkdirs())
            return dir;

        LOG.error("Not a directory: {}", dir);
        return new File(SystemUtils.USER_HOME, ".littleshoot");
    }
    
    /**
     * Returns whether or not this is LittleShoot Pro.
     * 
     * @return <code>true</code> if we're running Pro, otherwise
     * <code>false</code>
     */
    public static boolean isPro() {
        return IS_PRO;
    }

    public static File getLittleShootDir() {
        final File lsDir = new File(SystemUtils.USER_HOME, ".littleshoot");
        if (!lsDir.isDirectory()) {
            lsDir.mkdirs();
        }
        return lsDir;
    }

    public static boolean isTrue(final String varName) {
        final String prop = System.getProperty(varName);
        return isStringTrue(prop);
    }

    public static boolean isPropertyTrue(final String key) {
        final Properties props = getProps();
        final String prop = props.getProperty(key);
        return isStringTrue(prop);
    }
    
    public static boolean isStringTrue(final String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        return str.trim().equalsIgnoreCase("true");
    }

    public static File getPropsFile() {
        return new File(getLittleShootDir(), "littleshoot.properties");
    }

    private static Properties littleShootProps = null;
    
    public static Properties getProps() {
        if (littleShootProps != null) return littleShootProps;
        final File propsFile = getPropsFile();
        if (!propsFile.isFile()) {
            try {
                propsFile.createNewFile();
            } catch (IOException e) {
                LOG.error("Could not create props file?", e);
            }
        }
        littleShootProps = new Properties();
        Reader fr = null;
        try {
            fr = new FileReader(propsFile);
            littleShootProps.load(fr);
            return littleShootProps;
        } catch (final IOException e) {
            LOG.error("Should not happen", e);
            littleShootProps = null;
            return null;
        } finally {
            IOUtils.closeQuietly(fr);
        }
    }
    
    public static void saveProps(final Properties props) {
        final File file = getPropsFile();
        Writer fw = null;
        try {
            fw = new FileWriter(file);
            props.store(fw, "LittleShoot Properties File");
        } catch (final IOException e) {
            LOG.error("Could not write props!!", e);
        } finally {
            IOUtils.closeQuietly(fw);
        }
    }

    public static void setProperty(final String key, final String value) {
        final Properties props = getProps();
        props.setProperty(key, value);
        saveProps(props);
    }
    
    /**
     * Makes a native call with a full string argument that will be parsed
     * into separate command line tokens with white space delimiters. If your
     * command contains individual arguments with spaces, don't use this call.
     * 
     * @param fullCommand The full command as you would write it on the
     * command line.
     * @return Any return values from the command.
     */
    public static String nativeCall(final String fullCommand) {
        return nativeCall(StringUtils.split(fullCommand));
    }
    
    /**
     * Makes a native call with the specified commands, returning the result.
     * 
     * @param commands The commands separated into individual arguments.
     * @return Any return values from the command.
     */
    public static String nativeCall(final String... commands) {
        LOG.info("Running '{}'", Arrays.asList(commands));
        final ProcessBuilder pb = new ProcessBuilder(commands);
        try {
            final Process process = pb.start();
            final InputStream is = process.getInputStream();
            final String data = IOUtils.toString(is);
            LOG.info("Completed native call: '{}'\nResponse: '"+data+"'", 
                Arrays.asList(commands));
            /*
            final int ev = process.exitValue();
            if (ev != 0) {
                final String msg = "Process not completed normally! " + 
                    Arrays.asList(commands)+" Exited with: "+ev;
                System.err.println(msg);
                LOG.error(msg);
            } else {
                LOG.info("Process completed normally!");
            }
            */
            return data;
        } catch (final IOException e) {
            LOG.error("Error running commands: " + Arrays.asList(commands), e);
            return "";
        }
    }
    
    /**
     * Combines the specified arrays into a single array.
     * 
     * @param arrays The arrays to combine.
     * @return The combined arrays.
     */
    public static byte[] combine(final byte[]... arrays) {
        return combine(Arrays.asList(arrays));
    }
    
    /**
     * Combines the specified arrays into a single array.
     * 
     * @param arrays The arrays to combine.
     * @return The combined arrays.
     */
    public static byte[] combine(final Collection<byte[]> arrays) {
        int length = 0;
        for (final byte[] array : arrays) {
            length += array.length;
        }
        final byte[] joinedArray = new byte[length];
        
        int position = 0;
        for (final byte[] array : arrays) {
            System.arraycopy(array, 0, joinedArray, position, array.length);
            position += array.length;
        }
        return joinedArray;
    }
    

    private static final int SIZE_LIMIT = 2 ^ 16;
    
    public static byte[] encode(final byte[] key, final byte[] data) {
        if (data.length < SIZE_LIMIT) {
            return encodeSingleMessage(key, data);
        }
        
        final int numArrays = 
            (int) Math.ceil((double)data.length/(double)SIZE_LIMIT);
        final Collection<byte[]> arrays = new ArrayList<byte[]>(numArrays);

        int index = 0;
        for (int i = 0; i < numArrays; i++) {
            final int size;
            final int remaining = data.length -index;
            if (remaining < SIZE_LIMIT) {
                size = remaining;
            }
            else {
                size = SIZE_LIMIT;
            }
            final byte[] array = new byte[size];
            System.arraycopy(data, index, array, 0, array.length);
            final byte[] msg = encodeSingleMessage(key, array);
            arrays.add(msg);
            index += size;
        }
        return CommonUtils.combine(arrays);
    }

    public static byte[] encodeSingleMessage(final byte[] key, 
        final byte[] data) {
        /*
        0                   1                   2                   3
        0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
       |    Version    |         Message Length        |               
       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
       |                                                               |
       |                        Message (N bytes)                      |
       |                                                               |
       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
       |                          MAC (N bytes)                        |
       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
       */
        final SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        final Cipher cipher;
        final byte[] cipherText;
        try {
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            cipherText = cipher.doFinal(data);
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No AES?", e);
        } catch (final NoSuchPaddingException e) {
            throw new IllegalArgumentException("Wrong padding?", e);
        } catch (final InvalidKeyException e) {
            throw new IllegalArgumentException("Bad key?", e);
        } catch (final IllegalBlockSizeException e) {
            throw new IllegalArgumentException("Bad block size?", e);
        } catch (final BadPaddingException e) {
            throw new IllegalArgumentException("Bad padding?", e);
        }
        
        final byte[] version = new byte[] {1};
        final int big = (cipherText.length & 0x0000FF00) >> 8;
        final byte[] length = new byte[] {
            (byte) big, 
            (byte) (cipherText.length & 0x000000FF)
        };

        final Mac mac;
        try {
            mac = Mac.getInstance("hmacSHA256");
            mac.init(skeySpec);
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No HMAC 256?", e);
        } catch (final InvalidKeyException e) {
            throw new IllegalArgumentException("Bad key?", e);
        }

        mac.update(version);
        mac.update(length);
        mac.update(cipherText);
        final byte[] rawMac = mac.doFinal();
        return CommonUtils.combine(version, length, cipherText, rawMac);
    }

    public static void decode(final byte[] key, final InputStream is, 
        final DataListener dl) throws IOException {
        long lastCopied = 0;
        while (lastCopied != -1L) {
            final ByteArrayOutputStream versionAndSize = 
                new ByteArrayOutputStream(3);
            if (copy(is, versionAndSize, 3) == -1) {
                throw new IOException("Input closed!!");
            }
            final byte[] versionAndSizeBytes = versionAndSize.toByteArray();
            
            final int size = 
                (versionAndSizeBytes[1] << 8) + versionAndSizeBytes[2];            
            final ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
            if (copy(is, baos, size) == -1) {
                throw new IOException("Input closed!!");
            }
            final byte[] cipherText = baos.toByteArray();
            
            final ByteArrayOutputStream macOs = new ByteArrayOutputStream(32);
            if (copy(is, macOs, 32) == -1) {
                throw new IOException("Input closed!!");
            }
            
            final SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            final Cipher cipher;
            final byte[] plainText;
            try {
                cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, skeySpec);
                plainText = cipher.doFinal(cipherText);
            } catch (final NoSuchAlgorithmException e) {
                throw new IllegalArgumentException("No AES?", e);
            } catch (final NoSuchPaddingException e) {
                throw new IllegalArgumentException("No padding?", e);
            } catch (final InvalidKeyException e) {
                throw new IllegalArgumentException("Bad key?", e);
            } catch (final IllegalBlockSizeException e) {
                throw new IllegalArgumentException("Bad block size?", e);
            } catch (final BadPaddingException e) {
                throw new IllegalArgumentException("Bad padding?", e);
            }
            
            final byte[] mac = macOs.toByteArray();
            final Mac mac256;
            try {
                mac256 = Mac.getInstance("hmacSHA256");
                mac256.init(skeySpec);
            } catch (final NoSuchAlgorithmException e) {
                throw new IllegalArgumentException("No hmacSHA256?", e);
            } catch (final InvalidKeyException e) {
                throw new IllegalArgumentException("Bad key?", e);
            }
            mac256.update(versionAndSizeBytes);
            mac256.update(cipherText);
            final byte[] computedMac = mac256.doFinal();
            if (!Arrays.equals(computedMac, mac)) {
                throw new IllegalArgumentException("Macs don't match!!");
            }
            dl.onData(plainText);
        }
    }
    
    private static long copy(final InputStream in, final OutputStream out,
            final long originalByteCount) throws IOException {
        if (originalByteCount < 0) {
            throw new IllegalArgumentException("Invalid byte count: "
                    + originalByteCount);
        }
        final int DEFAULT_BUFFER_SIZE = 4096;
        final byte buffer[] = new byte[DEFAULT_BUFFER_SIZE];
        int len = 0;
        long written = 0;
        long byteCount = originalByteCount;
        try {
            while (byteCount > 0) {
                // len = in.read(buffer);
                if (byteCount < DEFAULT_BUFFER_SIZE) {
                    len = in.read(buffer, 0, (int) byteCount);
                } else {
                    len = in.read(buffer, 0, DEFAULT_BUFFER_SIZE);
                }

                if (len == -1) {
                    LOG.debug("Breaking on length = -1");
                    // System.out.println("Breaking on -1");
                    return -1;
                }

                byteCount -= len;
                LOG.info("Total written: " + written);
                out.write(buffer, 0, len);
                written += len;
                // LOG.debug("IoUtils now written: "+written);
            }
            // System.out.println("Out of while: "+byteCount);
            return written;
        } catch (final IOException e) {
            LOG.debug("Got IOException during copy after writing " + written
                    + " of " + originalByteCount, e);
            e.printStackTrace();
            throw e;
        } catch (final RuntimeException e) {
            LOG.debug("Runtime error after writing " + written + " of "
                    + originalByteCount, e);
            e.printStackTrace();
            throw e;
        } finally {
            out.flush();
        }
    }
    
    public static byte[] decode(final byte[] key, final byte[] msg) {
        /*
        0                   1                   2                   3
        0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
       |    Version    |         Message Length        |               
       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
       |                                                               |
       |                        Message (N bytes)                      |
       |                                                               |
       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
       |                          MAC (N bytes)                        |
       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
       */
        final byte version = msg[0];
        
        // This needs to be an int even though it's two bytes because shorts
        // are signed
        final int size = (msg[1] << 8) + msg[2];
        
        final byte[] cipherText = new byte[size];
        System.arraycopy(msg, 3, cipherText, 0, cipherText.length);
        
        final byte[] rawMac = new byte[32];
        System.arraycopy(msg, 3 + cipherText.length, rawMac, 0, rawMac.length);
        
        final SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        final Cipher cipher;
        final byte[] plainText;
        try {
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            plainText = cipher.doFinal(cipherText);
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No AES?", e);
        } catch (final NoSuchPaddingException e) {
            throw new IllegalArgumentException("No padding?", e);
        } catch (final InvalidKeyException e) {
            throw new IllegalArgumentException("Bad key?", e);
        } catch (final IllegalBlockSizeException e) {
            throw new IllegalArgumentException("Bad block size?", e);
        } catch (final BadPaddingException e) {
            throw new IllegalArgumentException("Bad padding?", e);
        }
        
        final byte[] length = new byte[] {
            msg[1], 
            msg[2]
        };
        
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
        mac256.update(version);
        mac256.update(length);
        mac256.update(cipherText);
        final byte[] mac = mac256.doFinal();

        // Now make sure the MACs match.
        if (!Arrays.equals(mac, rawMac)) {
            LOG.error("MACs don't match!!");
            throw new IllegalArgumentException("Macs don't match!!");
        }
        return plainText;
    }

    public static byte[] generateKey() {
        // TODO: Switch to 256 or higher.
        keyGenerator.init(128); 
        final SecretKey skey = keyGenerator.generateKey();
        return skey.getEncoded();
    }
    
    public static String generateBase64Key() {
        return Base64.encodeBase64String(generateKey());
    }

    public static byte[] decodeBase64(final String base64) {
        final byte[] body;
        if (StringUtils.isBlank(base64)) {
            LOG.error("No data!!");
            body = ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        else {
            body = Base64.decodeBase64(base64);
        }
        return body;
    }
}
