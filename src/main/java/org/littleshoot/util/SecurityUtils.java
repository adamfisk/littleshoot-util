package org.littleshoot.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for security. 
 */
public class SecurityUtils 
    {
    
    private static final Logger LOG = LoggerFactory.getLogger(SecurityUtils.class);
    
    /**     
     * HMAC/SHA1 Algorithm per RFC 2104.     
     */    
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    
    /**
     * Calculate the HMAC/SHA1 on a string.
     * 
     * @param canonicalString Data to sign
     * @param accessKey The secret access key to sign it with.
     * @return The base64-encoded RFC 2104-compliant HMAC signature.
     * @throws RuntimeException If the algorithm does not exist or if the key
     * is invalid -- both should never happen.
     */
    public static String signAndEncode(final String accessKey, 
        final String canonicalString)
        {
        if (StringUtils.isBlank(accessKey))
            {
            LOG.warn("Empty key!!");
            throw new IllegalArgumentException("Empty key");
            }
        //
        // Acquire an HMAC/SHA1 from the raw key bytes.
        final SecretKeySpec signingKey =
            new SecretKeySpec(accessKey.getBytes(), HMAC_SHA1_ALGORITHM);

        // Acquire the MAC instance and initialize with the signing key.
        final Mac mac;
        try 
            {
            mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            } 
        catch (final NoSuchAlgorithmException e) 
            {
            LOG.error("No SHA-1", e);
            throw new RuntimeException("Could not find sha1 algorithm", e);
            }
        try 
            {
            mac.init(signingKey);
            } 
        catch (final InvalidKeyException e) 
            {
            LOG.error("Bad key", e);
            // also should not happen
            throw new RuntimeException("Could not initialize the MAC algorithm", e);
            }

        // Compute the HMAC on the digest, and set it.
        final String b64 = 
            Base64.encodeBytes(mac.doFinal(canonicalString.getBytes()));

        return b64;
        }

    public static String hash(final String str)
        {
        return Sha1Hasher.hash(str);
        }
    }
