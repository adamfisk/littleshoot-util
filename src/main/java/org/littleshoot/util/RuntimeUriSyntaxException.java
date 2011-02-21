package org.littleshoot.util;

import java.net.URISyntaxException;

/**
 * A runtime version of the <code>URISyntaxException</code>.
 */
public class RuntimeUriSyntaxException extends RuntimeException
    {
    /**
     * Constructs a new runtime URI syntax exception.
     * 
     * @param cause The original checked exception.
     */
    public RuntimeUriSyntaxException (final URISyntaxException cause)
        {
        super (cause);
        }
    }
