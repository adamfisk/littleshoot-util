package org.littleshoot.util;

/**
 * A one-argument function.
 * 
 * @param <T>
 *      The type of the parameter.
 * @param <ReturnT>
 *      The return type of the function.
 */
public interface F1<T,ReturnT>
    {
    /**
     * Runs this function.
     *
     * @param arg
     *      The argument.
     *      
     * @return
     *      The return value of this function.
     */
    ReturnT run
            (T arg);
    }
