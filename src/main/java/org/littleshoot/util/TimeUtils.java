package org.littleshoot.util;

/**
 * Utility methods for times.
 */
public class TimeUtils
    {

    private TimeUtils() {}
    
    /**
     * Converts seconds into hours:minutes:seconds.
     * 
     * @param secs The number of seconds.
     * @return The formatted string.
     */
    public static String secondsToHoursMinutesSeconds(final long secs)
        {
        final double minutesRemaining = (secs / 60) % 60;
        final double hoursRemaining = Math.floor(secs / 60 / 60);
        final double secondsRemaining = secs % 60;
        
        final StringBuilder sb = new StringBuilder();
        if (hoursRemaining > 0.0)
            {
            sb.append((int)hoursRemaining);
            sb.append(" hrs, ");
            }
        if (minutesRemaining > 0.0)
            {
            sb.append((int)minutesRemaining);
            sb.append(" mins, ");
            }
        sb.append((int)secondsRemaining);
        sb.append(" secs");
        return sb.toString();
        }
    }
