package org.littleshoot.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of the thread utilities interface.
 */
public final class ThreadUtils {

    private ThreadUtils() {
    }

    /**
     * The log for this class.
     */
    private static final Logger LOG = LoggerFactory
            .getLogger(ThreadUtils.class);

    public static void safeWait(final Object object) {
        try {
            object.wait();
        } catch (final InterruptedException ie) {
            LOG.warn("Unexpected interrupted exception", ie);
        }
    }

    public static void safeWait(final Object object, final int millis) {
        try {
            object.wait(millis);
        } catch (final InterruptedException ie) {
            LOG.warn("Unexpected interrupted exception", ie);
        }
    }

    public static void safeSleep(final long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (final InterruptedException ie) {
            LOG.warn("Unexpected interrupted exception", ie);
        }
    }

    /**
     * Returns the stack trace as a string.
     * 
     * @return The stack trace as a string.
     */
    public static String dumpStack() {
        return dumpStack(new Exception("Stack Dump Generated Exception"));
    }

    /**
     * Returns the stack trace as a string.
     * 
     * @param cause
     *            The thread to dump.
     * @return The stack trace as a string.
     */
    public static String dumpStack(final Throwable cause) {
        if (cause == null) {
            return "Throwable was null";
        }
        final StringWriter sw = new StringWriter();
        final PrintWriter s = new PrintWriter(sw);

        // This is very close to what Thread.dumpStack does.
        cause.printStackTrace(s);

        final String stack = sw.toString();
        try {
            sw.close();
        } catch (final IOException e) {
            LOG.warn("Could not close writer", e);
        }
        s.close();
        return stack;
    }
}
