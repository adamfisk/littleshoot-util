package org.littleshoot.util;

/**
 * String utility methods not supplied in other string utility library classes.
 */
public class StringUtils
    {

    /**
     * Convert specified String to a byte array. 
     * 
     * @param value to convert to byte array
     * @return the byte array value
     */
    public static final byte[] toAsciiBytes(final String value) 
        {
        byte[] result = new byte[value.length()];
        for (int i = 0; i < value.length(); i++) 
            {
            result[i] = (byte) value.charAt(i);
            }
        return result;
        }
    }
