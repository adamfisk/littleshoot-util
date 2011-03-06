package org.littleshoot.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
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
        if (StringUtils.isBlank(prop)) {
            return false;
        }
        return prop.trim().equalsIgnoreCase("true");
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
}
