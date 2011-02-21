package org.littleshoot.util;

import java.net.ConnectException;

/**
 * A runtime wrapper around an <code>ConnectException</code>.
 */
public class RuntimeConnectException extends RuntimeIoException
    {
    /**
     * Constructs a new runtime wrapper for an <code>ConnectException</code>.
     * 
     * @param original
     *      The original <code>ConnectException</code> to wrap.
     */
    public RuntimeConnectException (final ConnectException original)
        {
        super (original);
        }
    }
