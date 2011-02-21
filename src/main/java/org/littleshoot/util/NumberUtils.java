package org.littleshoot.util;

import java.math.BigInteger;

/**
 * Number utility class. 
 */
public class NumberUtils
    {

    private NumberUtils()
        {
        // Should never be constructed.
        }

    /**
     * Returns whether the first {@link BigInteger} is bigger than the second.
     * 
     * @param big1 The first {@link BigInteger} to compare.
     * @param big2 The second {@link BigInteger} to compare.
     * @return <code>true</code> if the first {@link BigInteger} is bigger
     * than the second, otherwise <code>false</code>.
     */
    public static boolean isBigger(final BigInteger big1, final BigInteger big2)
        {
        final int compared = big1.compareTo(big2);
        if (compared > 0)
            {
            return true;
            }
        return false;
        }


    /**
     * Returns whether the first {@link BigInteger} is bigger than or equal
     * to the second.
     * 
     * @param big1 The first {@link BigInteger} to compare.
     * @param big2 The second {@link BigInteger} to compare.
     * @return <code>true</code> if the first {@link BigInteger} is bigger
     * than or equal to the second, otherwise <code>false</code>.
     */
    public static boolean isBiggerOrEqual(final BigInteger big1, 
        final BigInteger big2)
        {
        final int compared = big1.compareTo(big2);
        if (compared >= 0)
            {
            return true;
            }
        return false;
        }
    }
