package org.littleshoot.util;

/**
 * A pairing of a checked exception and its runtime wrapper.  This is used for
 * basic testing of runtime wrapper exceptions.
 */
public final class RuntimeExceptionPair
    {
    /**
     * Constructs a new runtime exception pair.
     * 
     * @param checked The checked that is wrapped.
     * @param runtime The wrapping runtime exception.
     */
    public RuntimeExceptionPair (final Exception checked,
                                 final RuntimeException runtime)
        {
        m_checked = checked;
        m_runtime = runtime;
        }
    
    /**
     * Returns the checked exception that is wrapped.
     * 
     * @return The checked exception that is wrapped.
     */
    public Exception checked ()
        {
        return (m_checked);
        }
    
    /**
     * Returns the wrapping runtime exception.
     * 
     * @return The wrapping runtime exception.
     */
    public RuntimeException runtime ()
        {
        return (m_runtime);
        }
    
    /**
     * The checked exception that is wrapped.
     */
    private final Exception m_checked;
    
    /**
     * The wrapping runtime exception.
     */
    private final RuntimeException m_runtime;
    }
