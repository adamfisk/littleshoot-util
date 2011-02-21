package org.littleshoot.util;

/**
 * An object reference.
 */
public interface ObjectRef
    {
    /**
     * Returns the object contained in this reference.
     * 
     * @return The object contained in this reference.
     */
    Object object ();
    
    /**
     * Sets the object contained in this reference.
     *
     * @param object The object to be contained in this reference.
     */
    void setObject (Object object);
    }