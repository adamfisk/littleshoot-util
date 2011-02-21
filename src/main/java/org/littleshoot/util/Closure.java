package org.littleshoot.util;

/**
 * A closure.
 */
public interface Closure<T>
    {
    /**
     * Executes this closure on a given object.
     * 
     * @param object
     *      The object.
     */
    void execute
            (T object);
    }
