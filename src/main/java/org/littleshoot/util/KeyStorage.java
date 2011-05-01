package org.littleshoot.util;

/**
 * Interface for classes that store secret keys.
 */
public interface KeyStorage {

    byte[] getWriteKey();
    
    byte[] getReadKey();
    
    void setReadKey(byte[] key);
}
