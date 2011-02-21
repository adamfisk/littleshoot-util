package org.littleshoot.util;

/**
 * Interface for classes that can determine the MIME type of a file name.
 */
public interface MimeType
    {
    
    /**
     * Returns the MIME type associated with a file name.
     * 
     * @param fileName The file name.
     * @return The MIME type.
     */
    String getMimeType(String fileName);
    }
