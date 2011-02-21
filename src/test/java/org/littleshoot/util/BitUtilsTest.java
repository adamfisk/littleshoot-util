package org.littleshoot.util;

import junit.framework.TestCase;

/**
 * Tests the {@link BitUtils} class.
 */
public class BitUtilsTest extends TestCase
    {

    public void testLongToByteArray() throws Exception
        {
        byte[] bytes = BitUtils.toByteArray(1L);
        
        assertEquals(0, bytes[0]);
        assertEquals(0, bytes[1]);
        assertEquals(0, bytes[2]);
        assertEquals(0, bytes[3]);
        assertEquals(0, bytes[4]);
        assertEquals(0, bytes[5]);
        assertEquals(0, bytes[6]);
        assertEquals(1, bytes[7]);
        
        bytes = BitUtils.toByteArray(256L);
        
        assertEquals(0, bytes[0]);
        assertEquals(0, bytes[1]);
        assertEquals(0, bytes[2]);
        assertEquals(0, bytes[3]);
        assertEquals(0, bytes[4]);
        assertEquals(0, bytes[5]);
        assertEquals(1, bytes[6]);
        assertEquals(0, bytes[7]);
        
        bytes = BitUtils.toByteArray(127L);
        
        assertEquals(0, bytes[0]);
        assertEquals(0, bytes[1]);
        assertEquals(0, bytes[2]);
        assertEquals(0, bytes[3]);
        assertEquals(0, bytes[4]);
        assertEquals(0, bytes[5]);
        assertEquals(0, bytes[6]);
        assertEquals(127, bytes[7]);
        
        bytes = BitUtils.toByteArray(0xffffL);
        
        assertEquals(0, bytes[0]);
        assertEquals(0, bytes[1]);
        assertEquals(0, bytes[2]);
        assertEquals(0, bytes[3]);
        assertEquals(0, bytes[4]);
        assertEquals(0, bytes[5]);
        assertEquals(-1, bytes[6]);
        assertEquals(-1, bytes[7]);
        
        bytes = BitUtils.toByteArray(0x01ffffL);
        
        assertEquals(0, bytes[0]);
        assertEquals(0, bytes[1]);
        assertEquals(0, bytes[2]);
        assertEquals(0, bytes[3]);
        assertEquals(0, bytes[4]);
        assertEquals(1, bytes[5]);
        assertEquals(-1, bytes[6]);
        assertEquals(-1, bytes[7]);
        
        bytes = BitUtils.toByteArray(0x09ffffffL);
        
        assertEquals(0, bytes[0]);
        assertEquals(0, bytes[1]);
        assertEquals(0, bytes[2]);
        assertEquals(0, bytes[3]);
        assertEquals(9, bytes[4]);
        assertEquals(-1, bytes[5]);
        assertEquals(-1, bytes[6]);
        assertEquals(-1, bytes[7]);
        }
    }
