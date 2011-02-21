package org.littleshoot.util;

/**
 * An optional object representing some object, meaning not none.
 * 
 * @param <T>
 *      The type of object this optional object holds.
 */
public interface Some<T> extends Optional<T>
    {
    /**
     * Returns the object.
     * 
     * @return The object.
     */
    T object ();
    }
