package org.littleshoot.util;

import org.apache.commons.lang.math.LongRange;

/**
 * Interface for listeners for range availability.
 */
public interface LongRangeListener
    {

    /**
     * Called when a specific range is complete.
     * 
     * @param range The completed range.
     */
    void onRangeComplete(LongRange range);
    }
