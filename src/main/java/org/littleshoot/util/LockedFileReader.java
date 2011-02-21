package org.littleshoot.util;

import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Interface for classes that read locked files.
 */
public interface LockedFileReader
    {

    void readFile(FileChannel fc) throws IOException;

    }
