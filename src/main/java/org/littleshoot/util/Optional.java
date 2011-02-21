package org.littleshoot.util;

/**
 * An optional object.  We use this in place of null where possible, since it
 * does a better job of forcing the 'null' value to be handled.
 * 
 * @param <T>
 *      The type of object this optional object holds.
 */
public interface Optional<T>
    {
    /**
     * Accepts a visitor to this optional object.
     * 
     * @param visitor The visitor.
     * @param <ReturnT> The type returned by the visitor.
     * @return The result of visiting this optional object with the given 
     * visitor.
     */
    <ReturnT> ReturnT accept (OptionalVisitor<ReturnT,T> visitor);
    }
