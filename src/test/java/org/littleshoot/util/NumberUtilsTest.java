package org.littleshoot.util;

import java.math.BigInteger;

import junit.framework.TestCase;

/**
 * Tests the number utils class.
 */
public class NumberUtilsTest extends TestCase
    {

    /**
     * Make sure isBigger works.
     * 
     * @throws Exception If any unexpected error occurs.
     */
    public void testIsBigger() throws Exception
        {
        final BigInteger big1 = new BigInteger("1");
        final BigInteger big2 = new BigInteger("2");
        final BigInteger big3 = new BigInteger("1");
        
        assertFalse(NumberUtils.isBigger(big1, big2));
        assertTrue(NumberUtils.isBigger(big2, big1));
        assertFalse(NumberUtils.isBigger(big1, big3));
        }
    

    /**
     * Test bigger or equals
     * 
     * @throws Exception If any unexpected error occurs.
     */
    public void testIsBiggerOrEqual() throws Exception
        {
        final BigInteger big1 = new BigInteger("1");
        final BigInteger big2 = new BigInteger("2");
        final BigInteger big3 = new BigInteger("1");
        
        assertFalse(NumberUtils.isBiggerOrEqual(big1, big2));
        assertTrue(NumberUtils.isBiggerOrEqual(big2, big1));
        assertTrue(NumberUtils.isBiggerOrEqual(big1, big3));
        }
    }
