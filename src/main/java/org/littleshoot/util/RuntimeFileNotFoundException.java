package org.littleshoot.util;

import java.io.FileNotFoundException;

/**
 * A runtime wrapper around a <code>FileNotFoundException</code>.
 */
public class RuntimeFileNotFoundException extends RuntimeIoException
    {
    /**
     * Constructs a new runtime wrapper for an
     * <code>FileNotFoundException</code>.
     * 
     * @param original The original <code>FileNotFoundException</code> to wrap.
     */
    public RuntimeFileNotFoundException (final FileNotFoundException original)
        {
        super (original);
        }
    }
