package org.littleshoot.util;

import java.io.IOException;

/**
 * A runtime wrapper around an <code>IOException</code>.
 */
public class RuntimeIoException extends RuntimeException
    {

    private static final long serialVersionUID = -6659518752904410507L;

    /**
     * Constructs a new runtime wrapper for an <code>IOException</code>.
     * 
     * @param original The original <code>IOException</code> to wrap.
     */
    public RuntimeIoException (final Exception original)
        {
        super (original);
        }

    /**
     * Constructs a new runtime wrapper for an <code>IOException</code>.
     * 
     * @param message The message to display with the exception.
     * @param original The original <code>IOException</code> to wrap.
     */
    public RuntimeIoException(final String message, final Exception original)
        {
        super(message, original);
        }

    /**
     * Creates a new {@link RuntimeIoException} with the specified message and 
     * no cause.
     * 
     * @param message The message.
     */
    public RuntimeIoException(final String message)
        {
        super(message);
        }
    }
