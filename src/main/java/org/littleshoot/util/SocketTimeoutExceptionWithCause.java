package org.littleshoot.util;

import java.net.SocketTimeoutException;

/**
 * A {@link SocketTimeoutException} that allows you to set the cause the 
 * constructor.
 */
public class SocketTimeoutExceptionWithCause extends SocketTimeoutException
    {

    /**
     * The serial version ID for serialization.
     */
    private static final long serialVersionUID = 3459279205017625728L;

    /**
     * Creates a new {@link SocketTimeoutException}.
     * 
     * @param message The message.
     * @param t The exception that caused this exception.
     */
    public SocketTimeoutExceptionWithCause(final String message, Throwable t)
        {
        super(message);
        initCause(t);
        }

    }
