package org.littleshoot.util;

/**
 * A visitor for an optional object.
 */
public interface OptionalVisitor<ReturnT,T>
    {
    /**
     * Visits the 'none' option.
     * 
     * @param none The 'none' option.
     */
    ReturnT visitNone (None<T> none);
    
    /**
     * Visits the 'some' option.
     * 
     * @param some The 'some' option.
     */
    ReturnT visitSome (Some<T> some);
    }
