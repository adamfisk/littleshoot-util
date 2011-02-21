package org.littleshoot.util;

/** 
 * A predicate. 
 *       
 * @param <T>
 *      The type of object expected by this predicate.
 */ 
public interface Predicate<T>
{        
    /**
     * Evaluates an object to a boolean value.
     *
     * @param object
     *      The object to evaluate.
     *
     * @return
     *      The result of the evaluation by this predicate.
     */
    boolean evaluate
            (T object);
}