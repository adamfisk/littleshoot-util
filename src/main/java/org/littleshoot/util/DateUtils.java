package org.littleshoot.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.httpclient.util.DateParseException;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Date utility functions.
 */
public class DateUtils
    {

    private static final Logger LOG = LoggerFactory.getLogger(DateUtils.class);
    
    public static String prettyS3Date(final String dateString)
        {
        final String formatString = "yyyy-MM-dd'T'HH:mm:ss";
        final SimpleDateFormat format =  
            new SimpleDateFormat(formatString, Locale.US);
        
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        try
            {
            final Date date = format.parse(dateString);
            return new SimpleDateFormat("MMM d, ''yy, h:mm:ss a").format(date);
            }
        catch (final ParseException e)
            {
            LOG.warn("Date not in expected format", e);
            return dateString;
            }
        }
    
    /**
     * Utility method for parsing an HTTP Date header.  This should match 
     * the following date pattern: EEE, d MMM yyyy HH:mm:ss Z
     * 
     * @param httpDateString The date string to parse.
     * @return The Date instance.
     * @throws DateParseException If the date is not in the expected format.
     */
    public static Date parseHttpDate(final String httpDateString) 
        throws DateParseException
        {
        /*
        final String formatString = "EEE, d MMM yyyy HH:mm:ss Z";
        final SimpleDateFormat format =  
            new SimpleDateFormat(formatString, Locale.US);
        
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        try
            {
            return format.parse(httpDateString);
            }
        catch (final ParseException e)
            {
            LOG.warn("Date not in expected format", e);
            throw new IOException("Date not in expected format");
            }
            */
        return DateUtil.parseDate(httpDateString);
        }
    
    /**
     * Generate an RFC 822 date for use in the Date HTTP header.
     * 
     * @return The HTTP Date string.
     */
    public static String createHttpDate() 
        {
        /*
        final String DateFormat = "EEE, dd MMM yyyy HH:mm:ss Z";
        final SimpleDateFormat format = 
            new SimpleDateFormat(DateFormat, Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(new Date());
        */
        return DateUtil.formatDate(new Date());
        }

    /**
     * Encodes date value into ISO8601 that can be compared 
     * lexicographically.
     * 
     * @return string representation of the date value for the current date.
     */
    public static String iso8601()
        {
        return iso8601(new Date());
        }

    public static String iso8601(final Date date)
        {
        final SimpleDateFormat dateFormatter = 
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        // Java doesn't handle ISO8601 nicely: need to add ':' manually 
        final String result = dateFormatter.format(date);
        return result.substring(0, result.length() - 2) + ":" + 
            result.substring(result.length() - 2);
        }

    /**
     * Converts the specified string in ISO 8601 format to a {@link Date}
     * instance.
     * 
     * @param dateString The string to convert.
     * @return The {@link Date} instance.
     * @throws ParseException If the date could not be parsed. 
     */
    public static Date iso8601ToDate(final String dateString) 
        throws ParseException
        {
        final SimpleDateFormat df = 
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        
        // We need to manually parse ISO 8601s ":".
        final String str = StringUtils.substringBeforeLast(dateString, ":") +
            StringUtils.substringAfterLast(dateString, ":");
        return df.parse(str);
        }
    }
