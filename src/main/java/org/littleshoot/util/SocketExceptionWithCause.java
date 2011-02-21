package org.littleshoot.util;

import java.net.SocketException;

/**
 * A {@link SocketException} that allows you to set the cause the 
 * constructor.
 */
public class SocketExceptionWithCause extends SocketException
    {

    /**
     * The serial version ID for serialization.
     */
    private static final long serialVersionUID = 3459279205017625728L;

    /**
     * Creates a new {@link SocketException}.
     * 
     * @param message The message.
     * @param t The exception that caused this exception.
     */
    public SocketExceptionWithCause(final String message, Throwable t)
        {
        super(message);
        initCause(t);
        }

    }
