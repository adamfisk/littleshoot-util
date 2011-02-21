package org.littleshoot.util;

/**
 * Utility interface for string callbacks.
 */
public interface StringListener
    {

    /**
     * Called when a string is available.
     * 
     * @param str The available string.
     */
    void onString(String str);
    }
