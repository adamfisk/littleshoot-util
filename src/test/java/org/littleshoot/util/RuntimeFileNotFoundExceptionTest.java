package org.littleshoot.util;

import java.io.FileNotFoundException;

/**
 * A test for the RuntimeFileNotFoundException wrapper.
 */
public final class RuntimeFileNotFoundExceptionTest
        extends RuntimeExceptionWrapperTest
    {
    protected RuntimeExceptionPair pair ()
        {
        final FileNotFoundException checked = new FileNotFoundException ();
        final RuntimeException runtime =
                new RuntimeFileNotFoundException (checked);
        
        return (new RuntimeExceptionPair (checked, runtime));
        }
    }
