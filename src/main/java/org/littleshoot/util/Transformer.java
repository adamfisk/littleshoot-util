package org.littleshoot.util;

/**
 * An interface to an object that can transform one object into another.
 * 
 * @param <T1>
 *      The type expected by this transformer.
 * @param <T2>
 *      The type returned by this transformer.
 */
public interface Transformer<T1,T2>
    {
    /**
     * Transforms one object into another object.
     *
     * @param object The object to transform.
     *
     * @return The result of the transformation.
     */
    T2 transform
            (T1 object);
    }