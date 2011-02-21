package org.littleshoot.util;

/**
 * Interface for classes that can determine resource types based on different
 * criteria.
 */
public interface ResourceTypeTranslator
    {

    /**
     * Determines the resource type for a file name.  Possible types are 
     * video, audio, document, application, etc.
     * 
     * @param fileName The name of the file to determine the type for.
     * @return The type of the file.
     */
    String getType(String fileName);

    /**
     * Returns whether or not the specified type is an audio or video type.
     * 
     * @param type The type to analyze.
     * @return <code>true</code> if it's an audio or video type, otherwise
     * <code>false</code>.
     */
    boolean isAudioOrVideo(String type);

    }
