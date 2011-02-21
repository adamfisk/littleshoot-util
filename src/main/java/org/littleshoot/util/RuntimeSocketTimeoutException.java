package org.littleshoot.util;

import java.net.SocketTimeoutException;

/**
 * A runtime wrapper around an <code>SocketTimeoutException</code>.
 */
public class RuntimeSocketTimeoutException extends RuntimeException
    {

    private static final long serialVersionUID = -8247391625942462601L;

    /**
     * Constructs a new runtime wrapper for a
     * <code>SocketTimeoutException</code>.
     * 
     * @param original The original <code>SocketTimeoutException</code> to wrap.
     */
    public RuntimeSocketTimeoutException (final SocketTimeoutException original)
        {
        super (original);
        }

    /**
     * Constructs a new runtime wrapper for a 
     * <code>SocketTimeoutException</code>.
     * 
     * @param message The message to display with the exception.
     * @param original The original <code>SocketTimeoutException</code> to wrap.
     */
    public RuntimeSocketTimeoutException(final String message, 
        final SocketTimeoutException original)
        {
        super(message, original);
        }

    /**
     * Creates a new {@link RuntimeSocketTimeoutException} with the specified 
     * message and no cause.
     * 
     * @param message The message.
     */
    public RuntimeSocketTimeoutException(final String message)
        {
        super(message);
        }
    }
