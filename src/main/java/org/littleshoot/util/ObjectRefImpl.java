package org.littleshoot.util;

/**
 * An implementation of the <code>ObjectRef</code> interface.
 */
public final class ObjectRefImpl implements ObjectRef
    {
    /**
     * The referenced object.
     */
    private Object m_object;

    /**
     * Constructs a new object reference.
     */
    public ObjectRefImpl
            ()
        {
        // Initially, the object reference refers to nothing.
        m_object = null;
        }

    /**
     * {@inheritDoc}
     */
    public Object object
            ()
        {
        return (m_object);
        }

    /**
     * {@inheritDoc}
     */
    public void setObject
            (final Object object)
        {
        m_object = object;
        }
    }
