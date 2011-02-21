package org.littleshoot.util;

import junit.framework.TestCase;

/**
 * A class to type runtime wrappers for checked exceptions.
 */
public abstract class RuntimeExceptionWrapperTest extends TestCase
    {
    /**
     * Tests that the runtime exception wrapper properly chains the original
     * checked exception.
     */
    public void testChaining ()
        {
        final RuntimeExceptionPair pair = pair ();
        
        assertEquals (pair.runtime ().getCause (), pair.checked ());
        }
    
    /**
     * Returns the pairing of a checked exception and its runtime wrapper.
     * 
     * @return The pairing of a checked exception and its runtime wrapper.
     */
    protected abstract RuntimeExceptionPair pair ();
    }
