package org.littleshoot.util;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtils
    {

    private static final Logger LOG = LoggerFactory.getLogger(HttpUtils.class);
    
    public static Map<String, String> toHeaderMap(final byte[] httpHeaders)
        {
        final String headers = utf8(httpHeaders);
        return toHeaderMap(headers);
        }
    
    public static Map<String, String> toHeaderMap(final String headers)
        {
        final Map<String, String> headerMap = new TreeMap<String, String>();
        
        if (StringUtils.isBlank(headers))
            {
            return Collections.emptyMap();
            }
        
        final Scanner scan = new Scanner(headers);
        scan.useDelimiter("\n");
        if (!scan.hasNext()) 
            {
            LOG.warn("No HTTP header data??");
            return headerMap;
            }
        // Read the status line...
        scan.next();
        while (scan.hasNext())
            {
            final String header = scan.next().trim();
            if (StringUtils.isBlank(header))
                {
                break;
                }
            final String headerName = 
                StringUtils.substringBefore(header, ":").trim().toLowerCase(Locale.US);
            final String headerValue = 
                StringUtils.substringAfter(header, ":").trim().toLowerCase(Locale.US);
            headerMap.put(headerName, headerValue);
            }
        return headerMap;
        }
    
    private static String utf8(final byte[] data)
        {
        try
            {
            return new String(data, "UTF-8");
            }
        catch (final UnsupportedEncodingException e)
            {
            LOG.error("No UTF-8???", e);
            return "";
            }
        }
    }
