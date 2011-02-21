package org.littleshoot.util;

import org.apache.commons.lang.ClassUtils;

/**
 * An implementation of the pair interface.
 * 
 * @param <T1>
 *      The type of the first element of this pair.
 * @param <T2>
 *      The type of the second element of this pair.
 */
public class PairImpl<T1,T2> implements Pair<T1,T2>
    {
    /**
     * The first object.
     */
    private final T1 m_first;

    /**
     * The second object.
     */
    private final T2 m_second;

    /**
     * Constructs a new pair.
     *
     * @param first
     *      The first object.
     * @param second
     *      The second object.
     */
    public PairImpl
            (final T1 first,
             final T2 second)
        {
        if (first == null)
            {
            throw new NullPointerException("Null first arg");
            }
        if (second == null)
            {
            throw new NullPointerException("Null second arg");
            }
        m_first = first;
        m_second = second;
        }

    /**
     * {@inheritDoc}
     */
    public T1 getFirst
            ()
        {
        return (m_first);
        }

    /**
     * {@inheritDoc}
     */
    public T2 getSecond
            ()
        {
        return (m_second);
        }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals
            (final Object other)
        {
        if (other instanceof Pair)
            {
            final Pair otherPair = (Pair) other;
            
            return otherPair.getFirst ().equals (m_first) &&
                    otherPair.getSecond ().equals (m_second);
            }
        else
            {
            return false;
            }
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode
            ()
        {
        final int prime = 203249;
        return (prime * m_first.hashCode ()) + (prime * m_second.hashCode ());
        }

    @Override
    public String toString()
        {
        final StringBuilder sb = new StringBuilder();
        sb.append(ClassUtils.getShortClassName(getClass()));
        sb.append(" ");
        sb.append(m_first);
        sb.append(" ");
        sb.append(m_second);
        return sb.toString();
        }
    
    }
