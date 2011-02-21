package org.littleshoot.util;

import java.util.Collection;

/**
 * Utilities for working with <code>Optional</code> objects.
 */
public interface OptionalUtils
    {
    /**
     * Returns an <code>Optional</code> from a possible <code>null</code>
     * object.  If the object is <code>null</code>, <code>None</code> is
     * returned.  Otherwise, a <code>Some</code> object containing the given
     * object is returned.
     *
     * @param object
     *      The object, possible <code>null</code>.
     *
     * @return
     *      If <code>object</code> is <code>null</code>, then <code>None</code>
     *      is returned, otherwise a <code>Some</code> containing
     *      <code>object</code> is returned.
     */
    Optional optionalOf
            (Object object);

    /**
     * Returns an <code>Optional</code> object created by transforming the
     * contents of another <code>Optional</code> object.  If the given
     * <code>Optional</code> is a <code>None</code>, then <code>None</code> is
     * returned.  If the given <code>Optional</code> is a <code>Some</code>,
     * then the contents of the <code>Some</code> are transformed and the
     * result wrapped in a <code>Some</code> is returned.
     *
     * @param optional
     *      The <code>Optional</code> object to be transformed.
     * @param transformer
     *      The transformer used to transform the contents of a
     *      <code>Some</code> object.
     *
     * @return
     *      An <code>Optional</code> object whose contents, if they exists,
     *      have been transformed by the given transformer.
     */
    <T1,T2> Optional<T2> transformSome
            (Optional<T1> optional,
             Transformer<T1,T2> transformer);

    /**
     * Returns whether an optional object is a <code>None</code> object.
     *
     * @param optional
     *      The optional object.
     *
     * @return
     *      True if <code>optional</code> is a <code>None</code>, false
     *      otherwise.
     */
    <T> boolean isNone
            (Optional<T> optional);

    /**
     * Returns whether an optional object is a <code>Some</code> object.
     *
     * @param optional
     *      The optional object.
     *
     * @return
     *      True if <code>optional</code> is a <code>Some</code>, false
     *      otherwise.
     */
    boolean isSome
            (Optional optional);

    /**
     * Returns the object stored in an optional object if one exists.
     * Otherwise, an exception is thrown.
     *
     * @param optional
     *      The optional object.
     *
     * @return
     *      The object stored in the optional object, if it exists.
     *
     * @throws IllegalArgumentException
     *      If the optional object is not a <code>Some</code> object.
     */
    <T> T someObject
            (Optional<T> optional) throws IllegalArgumentException;
    
    /**
     * Executes a given closure on an optional object.
     * 
     * @param <T>
     *      The type of object held by the optional object.
     *      
     * @param optional
     *      The optional object.
     * @param closure
     *      The closure to execute on the object held by the optional object, if
     *      it exists.
     */
    <T> void executeOnSome
            (Optional<T> optional,
             Closure<T> closure);
    
    /**
     * Filters out the <code>None</code> elements in a collection and returns a
     * collection of the objects inside the remaining <code>Some</code>
     * elements.
     * 
     * @param <T>
     *      The type of the objects inside the some objects.
     *      
     * @param c
     *      The collection to filter.
     *      
     * @return
     *      A collection of type <code>T</code> elements.
     */
    <T> Collection<T> filterNones
            (Collection<Optional<T>> c);
    }
