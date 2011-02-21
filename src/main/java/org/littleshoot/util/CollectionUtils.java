package org.littleshoot.util;

import java.util.Collection;

/**
 * Collection utilities.
 */
public interface CollectionUtils
    {
    /**  
     * Adds all of the elements in a given collection that satisfy a given
     * predicate to another given collection.
     *   
     * @param <T>
     *      The type of objects in the collection and expected by the predicate.
     *
     * @param collection
     *      The collection.
     * @param predicate  
     *      The predicate used to evaluate each element.
     * @param result
     *      The collection to which to add the elements that satisfy the
     *      predicate.
     */
    <T> void select
            (Collection<? extends T> collection,
             Predicate<T> predicate,
             Collection<T> result);

    /**
     * Returns a collection of all of the elements in a given collection that
     * satisfy a given predicate.
     *   
     * @param <T>
     *      The type of objects in the collection and expected by the predicate.
     *
     * @param collection
     *      The collection.
     * @param predicate
     *      The predicate used to evaluate each element.
     *   
     * @return
     *      A collection of all of the elements in a given collection that
     *      satisfy a given predicate.
     */ 
    <T> Collection<T> select
            (Collection<? extends T> collection,
             Predicate<T> predicate);
    
    /**
     * Maps the items in a collection using a given function.
     * 
     * @param <T1>
     *      The type of elements in the original collection.
     * @param <T2>
     *      The type to which we are mapping.
     * @param collection
     *      The collection to map.
     * @param f
     *      The mapping function.
     *      
     * @return
     *      The mapped collection.
     */
    <T1,T2> Collection<T2> map
            (Collection<? extends T1> collection,
             F1<T1,T2> f);
    
    /**
     * Left-associative fold function.
     * 
     * @param <T1>
     *      The type of the collection over which to fold.
     * @param <T2>
     *      The type returned by the folding function.
     *      
     * @param f
     *      The folding function.
     * @param initial
     *      The initial value for folding.
     * @param collection
     *      The collection.
     *      
     * @return
     *      The result of the folding.
     */
    <T1,T2> T2 foldl
            (F2<T2,T1,T2> f,
             T2 initial,
             Collection<? extends T1> collection);
    
    /**
     * Returns the sum of a collection of long integers.
     * 
     * @param c
     *      The collection of long integers.
     *      
     * @return
     *      The sum of the collection of long integers.
     */
    long sum
            (Collection<Long> c);
    
    /**
     * Executes the specified {@link Closure} on all elements of the
     * {@link Collection}.
     * 
     * @param <T> The type of elements in the {@link Collection}.
     * @param collection The {@link Collection} with elements to perform the
     * closure on.
     * @param closure The {@link Closure} to execute on each element.
     */
    <T> void forAllDo
            (Collection<? extends T> collection,
             Closure<T> closure);

    /**
     * Executes the specified {@link Closure} on all elements of the
     * {@link Collection}.  Also synchronizes on the {@link Collection}.
     * 
     * @param <T> The type of elements in the {@link Collection}.
     * @param collection The {@link Collection} with elements to perform the
     * closure on.
     * @param closure The {@link Closure} to execute on each element.
     */
    <T> void forAllDoSynchronized
            (Collection<? extends T> collection,
             Closure<T> closure);

    /**
     * Checks to see if any of the elements of the given {@link Collection}
     * matches the predicate.
     * 
     * @param <T> The type of elements in the {@link Collection}.
     * @param elements The {@link Collection} to check.
     * @param pred The {@link Predicate} determining if an element matches.
     * @return <code>true</code> if there's any element in the 
     * {@link Collection} matching the desired criteria.
     */
    <T> boolean matchesAny
            (Collection<? extends T> elements, 
             Predicate<T> pred);

    /**
     * Checks to see if all of the elements of the given {@link Collection}
     * matches the predicate.
     * 
     * @param <T> The type of elements in the {@link Collection}.
     * @param elements The {@link Collection} to check.
     * @param pred The {@link Predicate} determining if all elements match.
     * @return <code>true</code> if all elements in the 
     * {@link Collection} match the desired criteria.
     */
    <T> boolean matchesAll
            (Collection<? extends T> elements, 
             Predicate<T> pred);
    
    /**
     * Checks to see if any of the elements of the given {@link Collection}
     * matches the predicate.  Synchronizes on the {@link Collection}.
     * 
     * @param <T> The type of elements in the {@link Collection}.
     * @param elements The {@link Collection} to check.
     * @param pred The {@link Predicate} determining if an element matches.
     * @return <code>true</code> if there's any element in the 
     * {@link Collection} matching the desired criteria.
     */
    <T> boolean synchronizedMatchesAny
            (Collection<? extends T> elements, 
             Predicate<T> pred);

    /**
     * Selects the first element matching the desired criteria, or 
     * <code>null</code> if no matching element exists. Synchronizes on the 
     * {@link Collection}.
     * 
     * @param <T> The type of elements in the {@link Collection}.
     * @param collection The {@link Collection} of elements to search.
     * @param pred The {@link Predicate} determining a match.
     * @return The first matching element, or <code>null</code> if no
     * such element exists.
     */
    <T> T selectFirst
            (Collection<? extends T> collection, 
             Predicate<T> pred);

    /**
     * Selects the first element matching the desired criteria, or 
     * <code>null</code> if no matching element exists. Synchronizes on the 
     * {@link Collection}.
     * 
     * @param <T> The type of elements in the {@link Collection}.
     * @param collection The {@link Collection} of elements to search.
     * @param pred The {@link Predicate} determining a match.
     * @return The first matching element, or <code>null</code> if no
     * such element exists.
     */
    <T> T selectFirstSynchronized
            (Collection<? extends T> collection, 
             Predicate<T> pred);

    }
