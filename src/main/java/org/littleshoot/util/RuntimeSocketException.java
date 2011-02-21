package org.littleshoot.util;

import java.net.SocketException;

/**
 * A runtime wrapper around an <code>SocketException</code>.
 */
public class RuntimeSocketException extends RuntimeException
    {

    private static final long serialVersionUID = -7637044624101509336L;

    /**
     * Constructs a new runtime wrapper for a <code>SocketException</code>.
     * 
     * @param original The original <code>SocketException</code> to wrap.
     */
    public RuntimeSocketException (final SocketException original)
        {
        super (original);
        }

    /**
     * Constructs a new runtime wrapper for a 
     * <code>SocketException</code>.
     * 
     * @param message The message to display with the exception.
     * @param original The original <code>SocketException</code> to wrap.
     */
    public RuntimeSocketException(final String message, 
        final SocketException original)
        {
        super(message, original);
        }

    /**
     * Creates a new {@link RuntimeSocketException} with the specified 
     * message and no cause.
     * 
     * @param message The message.
     */
    public RuntimeSocketException(final String message)
        {
        super(message);
        }
    }
