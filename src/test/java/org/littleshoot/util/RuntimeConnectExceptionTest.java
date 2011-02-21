package org.littleshoot.util;

import java.net.ConnectException;

/**
 * A test for the RuntimeConnectException wrapper.
 */
public final class RuntimeConnectExceptionTest
        extends RuntimeExceptionWrapperTest
    {
    protected RuntimeExceptionPair pair ()
        {
        final ConnectException checked =
                new ConnectException ();

        final RuntimeException runtime =
                new RuntimeConnectException (checked);
        
        return (new RuntimeExceptionPair (checked, runtime));
        }
    }
