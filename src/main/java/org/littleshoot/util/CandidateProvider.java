package org.littleshoot.util;

import java.util.Collection;

/**
 * Interface providing candidates for any potential use.  The {@link Collection}
 * of candidates could be server to connect to, for example.
 * 
 * @param <T> The class of candidates.
 */
public interface CandidateProvider<T>
    {
    
    /**
     * Accessor for the {@link Collection} of candidates for the task at hand.
     * 
     * @return The {@link Collection} of candidates for the task at hand.
     */
    Collection<T> getCandidates();

    /**
     * Accessor for a single candidate.
     * 
     * @return A single candidate.
     */
    T getCandidate();
    }
