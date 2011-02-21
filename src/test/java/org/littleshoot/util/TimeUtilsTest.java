package org.littleshoot.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class TimeUtilsTest
    {

    @Test public void testSecsToHoursMinutesSecs() throws Exception
        {
        final long secs1 = 20L;
        final String hms1 = TimeUtils.secondsToHoursMinutesSeconds(secs1);
        assertEquals("20 secs", hms1);
        
        final long secs2 = 20000L;
        final String hms2 = TimeUtils.secondsToHoursMinutesSeconds(secs2);
        assertEquals("5 hrs, 33 mins, 20 secs", hms2);
        
        final long secs3 = 60L * 10L;
        final String hms3 = TimeUtils.secondsToHoursMinutesSeconds(secs3);
        assertEquals("10 mins, 0 secs", hms3);
        }
    }
