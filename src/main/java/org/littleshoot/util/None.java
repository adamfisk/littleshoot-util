package org.littleshoot.util;

/**
 * The optional object representing no object.
 * 
 * @param <T>
 *      The type of object this optional object holds.
 */
public interface None<T> extends Optional<T>
    {
    // This is just an empty type for the visitor pattern.
    }
