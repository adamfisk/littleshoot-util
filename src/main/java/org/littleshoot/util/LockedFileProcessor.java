package org.littleshoot.util;

/**
 * Interface for classes that process files that need to be locked during 
 * processing.
 */
public interface LockedFileProcessor
    {

    /**
     * Tells to processor to start checking the file for changes and to start
     * processing data.
     */
    void processFile();
    }
