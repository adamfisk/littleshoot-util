package org.littleshoot.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Utilities for common reader/writer functions.
 */
public final class MapUtils
    {

    /**
     * Removes the specified reader/writer from the values of the specified
     * reader/writer map.  Many times classes will contain maps of some key
     * to reader/writers, and this method simply allows those classes to easily
     * remove those values.
     * 
     * @param map The map mapping some key to reader/writers.
     * @param value The reader/writer to remove.
     * @return The key for the object removed, or <code>null</code> if nothing
     * was removed.
     */
    public static Object removeFromMapValues(final Map map, final Object value)
        {
        synchronized (map)
            {
            final Collection entries = map.entrySet();
            for (final Iterator iter = entries.iterator(); iter.hasNext();)
                {
                final Map.Entry entry = (Entry) iter.next();
                if (entry.getValue().equals(value))
                    {
                    final Object key = entry.getKey();
                    iter.remove();
                    return key;
                    }
                }
            return null;
            }
        }
    }
