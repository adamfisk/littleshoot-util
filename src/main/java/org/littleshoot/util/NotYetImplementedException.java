package org.littleshoot.util;

/**
 * An exception indicating that some functionality has not yet implemented.
 */
public class NotYetImplementedException extends RuntimeException
    {
    /**
     * Constructs a new not yet implemented exception.
     */
    public NotYetImplementedException ()
        {
        super ();
        }

    /**
     * Constructs a new not yet implemented exception.
     * 
     * @param message The message.
     */
    public NotYetImplementedException (final String message)
        {
        super (message);
        }

    /**
     * Constructs a new not yet implemented exception.
     * 
     * @param message The message.
     * @param cause   The cause of the exception (used for chaining).
     */
    public NotYetImplementedException (final String message,
                                       final Throwable cause)
        {
        super (message, cause);
        }

    /**
     * Constructs a new not yet implemented exception.
     * 
     * @param cause The cause of the exception (used for chaining).
     */
    public NotYetImplementedException (final Throwable cause)
        {
        super (cause);
        }
    }
