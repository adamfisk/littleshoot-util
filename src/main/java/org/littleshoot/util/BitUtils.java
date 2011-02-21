package org.littleshoot.util;


/**
 * Utility methods for bit twiddling.
 */
public class BitUtils
    {

    /**
     * Returns the bytes (big-endian) of an integer.
     *
     * @param i The integer.
     *
     * @return A 4-byte array of the bytes of the integer.
     */
    public static byte[] toByteArray(final int i)
        {
        final byte[] b = new byte[4];

        b[0] = (byte) ((i & 0xff000000) >>> 24);
        b[1] = (byte) ((i & 0x00ff0000) >>> 16);
        b[2] = (byte) ((i & 0x0000ff00) >>> 8);
        b[3] = (byte) ((i & 0x000000ff));

        return b;
        }
    
    /**
     * Converts the specified long to an array of bytes.
     * 
     * @param l The long to convert.
     * @return The array of bytes with the most significant byte first.
     */
    public static byte[] toByteArray(final long l)
        {
        final byte[] bytes = new byte[8];
        bytes[0] = (byte) ((l >>> 56) & 0xff);
        bytes[1] = (byte) ((l >>> 48) & 0xff);
        bytes[2] = (byte) ((l >>> 40) & 0xff);
        bytes[3] = (byte) ((l >>> 32) & 0xff);
        bytes[4] = (byte) ((l >>> 24) & 0xff);
        bytes[5] = (byte) ((l >>> 16) & 0xff);
        bytes[6] = (byte) ((l >>> 8) & 0xff);
        bytes[7] = (byte) ((l >>> 0) & 0xff);
        return bytes;
        }

    }
