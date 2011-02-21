package org.littleshoot.util;

import junit.framework.TestCase;

/**
 * A test case for the array utilities class.
 */
public final class ArrayUtilTest extends TestCase
    {
    /**
     * Tests the string subarray method.
     */
    public void testSubarray ()
        {
        final String[] array;
        final String[] subarray;
        
        array = new String[] {"zero", "one", "two", "three", "four", "five"};
        
        subarray = ArrayUtil.subarrayOf (array, 1, 5);
        
        assertEquals (4, subarray.length);
        
        for (int i = 0; i < 4; ++i)
            {
            assertEquals (array[i + 1], subarray[i]);
            }
        }
    }
