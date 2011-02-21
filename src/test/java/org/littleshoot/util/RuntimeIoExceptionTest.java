package org.littleshoot.util;

import java.io.IOException;

/**
 * A test for the RuntimeIoException wrapper.
 */
public final class RuntimeIoExceptionTest extends RuntimeExceptionWrapperTest
    {
    protected RuntimeExceptionPair pair ()
        {
        final IOException checked = new IOException ();
        final RuntimeException runtime = new RuntimeIoException (checked);
        
        return (new RuntimeExceptionPair (checked, runtime));
        }
    }
