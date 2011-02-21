package org.littleshoot.util;

import java.io.Serializable;

/**
 * An implementation of the Some interface.
 * 
 * @param <T>
 *      The type of object this optional object holds.
 */
public final class SomeImpl<T> implements Some<T>, Serializable
    {
    /**
     * The object.
     */
    private final T m_object;

    /**
     * Constructs a new optional object with some object value.
     * 
     * @param object
     *      The object value.
     */
    public SomeImpl
            (final T object)
        {
        m_object = object;
        }

    /**
     * {@inheritDoc}
     */
    public T object
            ()
        {
        return (m_object);
        }

    /**
     * {@inheritDoc}
     */
    public <ReturnT> ReturnT accept
            (final OptionalVisitor<ReturnT,T> visitor)
        {
        return visitor.visitSome (this);
        }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals (final Object other)
        {
        if (!(other instanceof Some))
            {
            return (false);
            }
        else if (other == this)
            {
            return (true);
            }
        else
            {
            try
                {
                final Some<T> otherSome = (Some<T>) other;
            
                return (otherSome.object ().equals (m_object));
                }
            catch (final ClassCastException e)
                {
                return false;
                }
            }
        }
    }
