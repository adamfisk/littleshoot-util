package org.littleshoot.util;

import java.util.Collection;
import java.util.LinkedList;

/**
 * An implementation of the collection utilities interface.
 */
public class CollectionUtilsImpl implements CollectionUtils
    {
    /**
     * {@inheritDoc}
     */
    public <T> void select
            (final Collection<? extends T> collection,
             final Predicate<T> predicate,
             final Collection<T> result)
        {    
        for (final T current : collection)
            {
            if (predicate.evaluate (current))
                {
                result.add (current);
                }
            }
        }
    
    /**
     * {@inheritDoc}
     */
    public <T> Collection<T> select
        (final Collection<? extends T> collection,
         final Predicate<T> predicate)
         {
        final LinkedList<T> result = new LinkedList<T> ();
    
        select (collection,predicate,result);
        
        return result;
         }
    
    
    /**
     * {@inheritDoc}
     */
    public <T> void forAllDo
            (final Collection<? extends T> collection, 
             final Closure<T> closure)
        {
        for (final T current : collection)
            {
            closure.execute(current);
            }
        }
  
    /**
     * {@inheritDoc}
     */
    public <T> void forAllDoSynchronized
            (final Collection<? extends T> collection, 
             final Closure<T> closure)
        {
        synchronized (collection)
            {
            forAllDo(collection, closure);
            }
        }
    
    /**
     * {@inheritDoc}
     */
    public <T1,T2> Collection<T2> map
            (final Collection<? extends T1> collection,
             final F1<T1,T2> f)
         {
         final Collection<T2> result = new LinkedList<T2> ();
         
         for (final T1 object : collection)
             {
             result.add (f.run (object));
             }
         
         return result;
         }
    
    /**
     * {@inheritDoc}
     */
    public <T1,T2> T2 foldl
            (final F2<T2,T1,T2> f,
             final T2 initial,
             final Collection<? extends T1> collection)
        {
        T2 result = initial;
        
        for (final T1 current : collection)
            {
            result = f.run (result, current);
            }
        
        return result;
        }

    /**
     * {@inheritDoc}
     */
    public long sum
            (final Collection<Long> c)
        {
        final F2<Long,Long,Long> f = new F2<Long,Long,Long> ()
            {
            public Long run
                    (final Long x,
                     final Long y)
                {
                return x + y;
                }
            };
            
        return foldl (f, 0L, c);
        }

    public <T> boolean matchesAny
            (final Collection<? extends T> collection, final Predicate<T> pred)
        {
        for (final T current : collection)
            {
            if (pred.evaluate(current))
                {
                return true;
                }
            }
        return false;
        }
    
    public <T> boolean matchesAll
            (final Collection<? extends T> collection, final Predicate<T> pred)
        {
        for (final T current : collection)
            {
            if (!pred.evaluate(current))
                {
                return false;
                }
            }
        return true;
        }

    public <T> boolean synchronizedMatchesAny
            (final Collection<? extends T> elements, final Predicate<T> pred)
        {
        synchronized (elements)
            {
            return matchesAny(elements, pred);
            }
        }

    public <T> T selectFirst
            (final Collection<? extends T> collection, final Predicate<T> pred)
        {
        for (final T current : collection)
            {
            if (pred.evaluate(current))
                {
                return current;
                }
            }
        return null;
        }

    public <T> T selectFirstSynchronized
            (final Collection<? extends T> collection, final Predicate<T> pred)
        {
        synchronized (collection)
            {
            return selectFirst(collection, pred);
            }
        }

    }
