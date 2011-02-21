package org.littleshoot.util;

/**
 * Utilities for working with arrays.
 */
public final class ArrayUtil
    {
    /**
     * Returns a new array with the elements between [start, end).
     *
     * @param array
     *      The array to get a subarray of.
     * @param start
     *      The start index (inclusive).
     * @param end
     *      The end index (exclusive);
     *
     * @return
     *      A new array with the elements between [start, end).
     */
    public static String[] subarrayOf
            (final String[] array,
             final int start,
             final int end)
        {
        final int subarraySize = end - start;
        final String[] subarray = new String[subarraySize];

        for (int i = 0; i < subarraySize; ++i)
            {
            subarray[i] = array[i + start];
            }

        return (subarray);
        }
    }
