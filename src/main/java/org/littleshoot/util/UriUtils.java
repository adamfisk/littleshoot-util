package org.littleshoot.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * URI manipulation utilities and helpers class.
 */
public class UriUtils
    {
    
    private static final Logger LOG = LoggerFactory.getLogger(UriUtils.class);
    
    private UriUtils()
        {
        
        }
    
    /**
     * Returns a string that has been encoded for use in a URL.
     * 
     * @param s The string to encode.
     * @return The given string encoded for use in a URL.
     */
    public static String urlNonFormEncode(final String s)
        {
        if (StringUtils.isBlank(s))
            {
            LOG.warn("Could not encode blank string");
            throw new IllegalArgumentException("Blank string");
            }
        try
            {
            // We use the HttpClient encoder because the java URLEncoder
            // class uses form encoding.
            return URIUtil.encodeQuery(s, "UTF-8");
            }
        catch (final URIException e)
            {
            LOG.error ("Could not encode: " + s, e);
            
            // This really should never happen.
            assert false;
            return s;
            }
        }
    
    /**
     * Returns a string that has been encoded for use in a URL.
     * 
     * @param s The string to encode.
     *      
     * @return The given string encoded for use in a URL.
     */
    public static String urlFormEncode(final String s)
        {
        if (StringUtils.isBlank(s))
            {
            LOG.warn("Could not encode blank string");
            throw new IllegalArgumentException("Blank string");
            }
        try
            {
            return URLEncoder.encode(s, "UTF-8");
            }
        catch (final UnsupportedEncodingException e)
            {
            LOG.error ("Could not encode: " + s, e);            
            return s;
            }
        }
    
    /**
     * Returns a string that has been decoded.
     * 
     * @param s The string to decode.
     *      
     * @return The string to decode.
     */
    public static String urlFormDecode(final String s)
        {
        if (StringUtils.isBlank(s))
            {
            LOG.warn("Could not encode blank string");
            throw new IllegalArgumentException("Blank string");
            }
        try
            {
            return URLDecoder.decode(s, "UTF-8");
            }
        catch (final UnsupportedEncodingException e)
            {
            LOG.error ("Could not encode: " + s, e);            
            return s;
            }
        }
    
    /**
     * Returns a key/value pair string for URL parameter encoding.
     * 
     * @param param The parameter key/value pair.
     *      
     * @return A key/value pair string for URL parameter encoding.
     */
    private static String getKeyValuePair(final Pair<String,String> param, 
        final boolean formEncoding)
        {
        try 
            {
            return getKeyValuePair(param.getFirst(), param.getSecond(), 
                formEncoding);
            }
        catch (final Exception e)
            {
            LOG.error("Could not get encoding for: {}", param, e);
            return "unknown=unknown";
            }
        }
 
    /**
     * Returns a key/value pair string for URL parameter encoding.
     * 
     * @param param The parameter key/value pair.
     *      
     * @return A key/value pair string for URL parameter encoding.
     */
    private static String getKeyValuePair(final Entry<String,String> param, 
        final boolean formEncoding)
        {
        try 
            {
            return getKeyValuePair(param.getKey(),param.getValue(),formEncoding);
            }
        catch (final Exception e)
            {
            LOG.error("Could not get encoding for: {}", param, e);
            return "unknown=unknown";
            }
        }
    

    public static String appendParam(final String url, final String name, 
        final String value)
        {
        return url + "&" + getKeyValuePair(name, value, true);
        }
    
    /**
     * Returns a key/value pair string for URL parameter encoding.
     * 
     * @param parameter The parameter key/value pair.
     *      
     * @return A key/value pair string for URL parameter encoding.
     */
    private static String getKeyValuePair(final String key, final String value, 
        final boolean formEncoding)
        {
        final StringBuilder sb = new StringBuilder ();
        
        final String firstEncoded;
        final String secondEncoded;
        if (formEncoding)
            {
            firstEncoded = urlFormEncode (key);
            secondEncoded = urlFormEncode (value);
            }
        else
            {
            firstEncoded = urlNonFormEncode (key);
            secondEncoded = urlNonFormEncode (value);
            }
        sb.append (firstEncoded);
        sb.append ('=');
        sb.append (secondEncoded);
        
        return sb.toString ();
        }
    
    /**
     * Returns a string that encodes a collection of key/value parameters for
     * URL use.
     * 
     * @param parameters The collection of key/value parameters.
     *      
     * @return A string that encodes the collection of key/value parameters for 
     * URL use.
     */
    private static String getUrlParameters(
        final Collection<Pair<String,String>> parameters, 
        final boolean formEncoding)
        {
        final StringBuilder sb = new StringBuilder ();
        final Iterator<Pair<String,String>> i = parameters.iterator ();
        
        if (i.hasNext ())
            {
            final Pair<String,String> first = i.next ();
            
            sb.append ('?');
            sb.append (getKeyValuePair (first, formEncoding));
            
            while (i.hasNext ())
                {
                final Pair<String,String> current = i.next ();
                
                sb.append ('&');
                sb.append (getKeyValuePair (current, formEncoding));
                }
            
            return sb.toString ();
            }
        else
            {
            return sb.toString ();
            }
        }
    
    private static String getUrlParameters(final Map<String, String> paramMap,
        final boolean formEncoding)
        {
        final StringBuilder sb = new StringBuilder ();
        final Iterator<Entry<String, String>> i = 
            paramMap.entrySet().iterator ();
        
        if (i.hasNext ())
            {
            appendFirstValid(sb, i, formEncoding);
            
            while (i.hasNext ())
                {
                final Entry<String, String> current = i.next ();
                if (isValid(current))
                    {
                    sb.append ('&');
                    sb.append (getKeyValuePair (current, formEncoding));
                    }
                }
            
            return sb.toString ();
            }
        else
            {
            return sb.toString ();
            }
        }

    private static void appendFirstValid(StringBuilder sb,
        Iterator<Entry<String, String>> i, boolean formEncoding)
        {
        while (i.hasNext())
            {
            final Entry<String, String> entry = i.next ();
            if (isValid(entry))
                {
                sb.append ('?');
                sb.append (getKeyValuePair (entry, formEncoding));
                return;
                }
            }
        }

    private static boolean isValid(final Entry<String, String> entry)
        {
        if (StringUtils.isBlank(entry.getKey()) || 
            StringUtils.isBlank(entry.getValue()))
            {
            LOG.debug("Blank entry for key:" + entry.getKey() + 
                "/value:" + entry.getValue());
            return false;
            }
        return true;
        }

    /**
     * Creates a new URL string from the specified base URL and parameters.
     * 
     * @param baseUrl The base URL excluding parameters.
     * @param params The parameters.
     * @return The full URL string.
     */
    public static String newUrl(final String baseUrl, 
        final Collection<Pair<String, String>> params)
        {
        final StringBuilder sb = new StringBuilder();
        sb.append(baseUrl);
        sb.append(getUrlParameters(params, true));
        return sb.toString();
        }
    
    /**
     * Creates a new URL string from the specified base URL and parameters.
     * 
     * @param baseUrl The base URL excluding parameters.
     * @param paramMap The parameters.
     * @return The full URL string.
     */
    public static String newUrl(final String baseUrl, 
        final Map<String, String> paramMap)
        {
        final StringBuilder sb = new StringBuilder();
        sb.append(baseUrl);
        sb.append(getUrlParameters(paramMap, true));
        return sb.toString();
        }
    
    /**
     * Creates a new URL string from the specified base URL and parameters 
     * encoded in non-form encoding, www-urlencoded.
     * 
     * @param baseUrl The base URL excluding parameters.
     * @param params The parameters.
     * @return The full URL string.
     */
    public static String newWwwUrlEncodedUrl(final String baseUrl,
        final Collection<Pair<String, String>> params)
        {
        final StringBuilder sb = new StringBuilder();
        sb.append(baseUrl);
        sb.append(getUrlParameters(params, false));
        return sb.toString();
        }
 
    /**
     * Creates a new URL string from the specified base URL and parameters 
     * encoded in non-form encoding, www-urlencoded.
     * 
     * @param baseUrl The base URL excluding parameters.
     * @param paramMap The parameters.
     * @return The full URL string.
     */
    public static String newWwwUrlEncodedUrl(final String baseUrl,
        final Map<String, String> paramMap)
        {
        final StringBuilder sb = new StringBuilder();
        sb.append(baseUrl);
        sb.append(getUrlParameters(paramMap, false));
        return sb.toString();
        }

    /**
     * Returns a pair of strings created from two strings.
     * 
     * @param s1 The first string.
     * @param s2 The second string.
     * @return A pair of strings created from the two given strings.
     */
    public static Pair<String,String> pair (final String s1, final String s2)
        {        
        if (StringUtils.isBlank(s1))
            {
            LOG.warn("Blank first arg");
            }
        if (StringUtils.isBlank(s2))
            {
            LOG.warn("Blank second arg for: "+s1);
            }
        return new PairImpl<String,String> (s1, s2);
        }

    /**
     * Returns a pair of strings created from a string name and a long value.
     * 
     * @param name The string name.
     * @param value The long value.
     * @return A pair of strings.
     */
    public static Pair<String, String> pair (final String name, 
        final long value)
        {
        return pair(name, String.valueOf(value));
        }
    
    /**
     * Returns a pair of strings created from a string name and a boolean 
     * value.
     * 
     * @param name The string name.
     * @param value The boolean value.
     * @return A pair of strings.
     */
    public static Pair<String, String> pair (final String name, 
        final boolean value)
        {
        return pair(name, String.valueOf(value));
        }

    /**
     * Returns a pair of strings created from a string name and a URI 
     * value.
     * 
     * @param name The string name.
     * @param value The URI value.
     * @return A pair of strings.
     */
    public static Pair<String, String> pair(final String name, final URI value)
        {
        return pair(name, value.toASCIIString());
        }

    /**
     * Strips the host from a URI string. This will turn "http://host.com/path"
     * into "/path".
     * 
     * @param uri The URI to transform.
     * @return A string with the URI stripped.
     */
    public static String stripHost(final String uri)
        {
        if (!uri.startsWith("http")) 
            {
            // It's likely a URI path, not the full URI (i.e. the host is 
            // already stripped).
            return uri;
            }
        final String noHttpUri = StringUtils.substringAfter(uri, "://");
        final int slashIndex = noHttpUri.indexOf("/");
        if (slashIndex == -1)
            {
            return "/";
            }
        final String noHostUri = noHttpUri.substring(slashIndex);
        return noHostUri;
        }
    }
