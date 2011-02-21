package org.littleshoot.util;

import java.nio.channels.FileChannel;

/**
 * Utility interface for executing methods with a lock on a file.
 */
public interface LockedFileRunner
    {

    /**
     * Allows an implementation to operation on the given file channel with
     * assurance that we hold the lock on the file.
     * 
     * @param fc The {@link FileChannel} we hold the lock on.
     */
    void callWithLock(FileChannel fc);
    };
