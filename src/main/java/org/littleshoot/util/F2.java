package org.littleshoot.util;

/**
 * A two-argument function.
 * 
 * @param <T0>
 *      The type of the first parameter.
 * @param <T1>
 *      The type of the second parameter.
 * @param <ReturnT>
 *      The return type of the function.
 */
public interface F2<T0,T1,ReturnT>
    {
    /**
     * Runs this function.
     *
     * @param arg0
     *      The first argument.
     * @param arg1
     *      The second argument.
     *      
     * @return
     *      The return value of this function.
     */
    ReturnT run
            (T0 arg0,
             T1 arg1);
    }
