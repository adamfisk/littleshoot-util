package org.littleshoot.util;

import java.io.IOException;

/**
 * An {@link IOException} that allows you to set the cause the constructor.
 */
public class IoExceptionWithCause extends IOException
    {

    /**
     * The serial version ID for serialization.
     */
    private static final long serialVersionUID = 2951182356590396483L;

    /**
     * Creates a new {@link IOException}.
     * 
     * @param message The message.
     * @param t The exception that caused this exception.
     */
    public IoExceptionWithCause(String message, Throwable t)
        {
        super(message);
        initCause(t);
        }

    }
