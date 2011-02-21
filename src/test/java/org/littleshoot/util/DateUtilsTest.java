package org.littleshoot.util;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;


public class DateUtilsTest
    {

    @Test public void testS3Dates() throws Exception
        {
        final String date = "2008-01-31T18:24:41.000Z";
        
        // Just to see what the date will look like.
        final String parsed = DateUtils.prettyS3Date(date);
        //System.out.println(parsed);
        }
    
    @Test public void testIso8601Date() throws Exception
        {
    
        final String dateString = DateUtils.iso8601();
        final Date date = DateUtils.iso8601ToDate(dateString);
        assertEquals(dateString, DateUtils.iso8601(date));
        }
    }
