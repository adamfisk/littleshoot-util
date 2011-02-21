package org.littleshoot.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class ThreadUtilsTest
    {

    @Test public void testStack() throws Exception
        {
        final String stack = ThreadUtils.dumpStack();
        assertTrue(stack.startsWith("java.lang.Exception: Stack trace"));
        }
    }
