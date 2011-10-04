package org.littleshoot.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for generating SHA-1 file hashes.  Formats SHA-1s as URNs, as 
 * specified in RFC 2141 and according to MAGNET conventions.
 */
public final class Sha1Hasher
    {
    
    /**
     * Logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Sha1Hasher.class);
    
    private Sha1Hasher()
        {
        // Should not be constructed.
        }
    
    /**
     * Create a new SHA1 hash URI for the specified file on disk.
     *
     * @param file the file to construct the hash from
     * @return The SHA1 hash as a URI.
     * @throws IOException If there is an error creating the hash or if the 
     * specified algorithm cannot be found.
     */
    public static URI createSha1Urn(final File file) throws IOException
        {
        LOG.debug("Creating SHA1 URN.");
        if (file.length() == 0L) 
            {
            throw new IOException("Cannot publish empty files!!");
            }
        return createSha1Urn(new FileInputStream(file));
        }
    
    /**
     * Create a new SHA1 hash URI for the specified {@link ByteBuffer}.
     *
     * @param buf The bytes to create the SHA-1 for.
     * @return The SHA1 hash as a URI.
     * @throws IOException If there is an error creating the hash or if the 
     * specified algorithm cannot be found.
     */
    public static URI createSha1Urn(final byte[] buf) throws IOException
        {
        return createSha1Urn(ByteBuffer.wrap(buf));
        }
    
    /**
     * Create a new SHA1 hash URI for the specified {@link ByteBuffer}.
     *
     * @param buf The {@link ByteBuffer} to create the SHA-1 for.
     * @return The SHA1 hash as a URI.
     * @throws IOException If there is an error creating the hash or if the 
     * specified algorithm cannot be found.
     */
    public static URI createSha1Urn(final ByteBuffer buf) throws IOException
        {
        final MessageDigest md = new Sha1();
        md.update(buf);

        final byte[] sha1 = md.digest();

        try
            {
            // preferred casing: lowercase "urn:sha1:", uppercase encoded value
            // note that all URNs are case-insensitive for the "urn:<type>:" part,
            // but some MAY be case-sensitive thereafter (SHA1/Base32 is case 
            // insensitive)
            return new URI("urn:sha1:"+Base32.encode(sha1));
            }
        catch (final URISyntaxException e)
            {
            // This should never happen.
            LOG.error("Could not encode SHA-1", e);
            throw new IOException("bad uri: "+Base32.encode(sha1));
            }  
        }
    
    
    /**
     * Create a new SHA1 hash URI for the specified {@link InputStream}.
     *
     * @param is The {@link InputStream} to create the SHA-1 for.
     * @return The SHA1 hash as a URI.
     * @throws IOException If there is an error creating the hash or if the 
     * specified algorithm cannot be found.
     */
    public static URI createSha1Urn(final InputStream is) throws IOException
        {
        
        /*
        try
            {
            md = MessageDigest.getInstance("SHA-1");
            }
        catch (final NoSuchAlgorithmException e)
            {
            LOG.error("No SHA-1??", e);
            return null;
            }
            */
        
        // We use this SHA-1 implementation because it's more than twice as 
        // fast.
        return createSha1Urn(is, new Sha1());
        }
    
    /**
     * Create a new SHA1 hash URI for the specified {@link InputStream}.
     *
     * @param is The {@link InputStream} to create the SHA-1 for.
     * @param md The {@link MessageDigest} class to use.
     * @return The SHA1 hash as a URI.
     * @throws IOException If there is an error creating the hash or if the 
     * specified algorithm cannot be found.
     */
    public static URI createSha1Urn(final InputStream is, 
        final MessageDigest md) throws IOException
        {
        LOG.debug("Creating SHA-1");
        // Note: The size of this array is not the bottleneck.
        final byte[] buffer = new byte[65536];
        int read;
        
        try 
            {
            while ((read = is.read(buffer)) != -1) 
                {
                //long start = System.currentTimeMillis();
                md.update(buffer,0,read);
                //progress.addInt( read );
                /*
                if (SystemUtils.getIdleTime() < MIN_IDLE_TIME) 
                    {
                    long end = System.currentTimeMillis();
                    long interval = end - start;
                    if (LOG.isTraceEnabled())
                        {
                        LOG.trace("Accessed idle time: "+SystemUtils.getIdleTime()+
                            " interval: "+interval);
                        }
                    if (interval > 0)
                        Thread.sleep(interval * 3);
                    else
                        Thread.yield();
                    }
                    */
                 }
            } 
        
        finally 
            {     
            IOUtils.closeQuietly(is);
            }

        final byte[] sha1 = md.digest();

        LOG.debug("Done creating SHA-1");
        try
            {
            // preferred casing: lowercase "urn:sha1:", uppercase encoded value
            // note that all URNs are case-insensitive for the "urn:<type>:" part,
            // but some MAY be case-sensitive thereafter (SHA1/Base32 is case 
            // insensitive)
            return new URI("urn:sha1:"+Base32.encode(sha1));
            }
        catch (final URISyntaxException e)
            {
            // This should never happen.
            LOG.error("Could not encode SHA-1", e);
            throw new IOException("bad uri: "+Base32.encode(sha1));
            }  
        }

    /**
     * Creates a base 64 encoded SHA-1 hash of the specified string.
     * 
     * @param str The string to hash.
     * @return The hashed and encoded string.
     */
    public static String hash(final String str)
        {
        try
            {
            final MessageDigest md = new Sha1();
            final byte[] hashed = md.digest(str.getBytes("UTF-8"));
            final byte[] encoded = Base64.encodeBase64(hashed);
            return new String(encoded, "UTF-8");
            }
        catch (final UnsupportedEncodingException e)
            {
            LOG.error("Encoding error??", e);
            return "";
            }
        }
    }
