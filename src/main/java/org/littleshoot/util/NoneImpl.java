package org.littleshoot.util;

import java.io.Serializable;

/**
 * An implementation of the None interface.
 * 
 * @param <T>
 *      The type of object this optional object holds.
 */
public final class NoneImpl<T> implements None<T>, Serializable
    {
    /**
     * {@inheritDoc}
     */
    public <ReturnT> ReturnT accept
            (final OptionalVisitor<ReturnT,T> visitor)
        {
        return visitor.visitNone (this);
        }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals
            (final Object other)
        {
        return ((this == other) || (other instanceof None));
        }
    }
