package org.littleshoot.util;

import java.io.File;
import java.nio.channels.FileChannel;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for detecting file changes.  When a change is detected, this
 * class obtains an exclusive read/write lock on the file and notifies 
 * subclasses of the change.  The subclass MUST read the whole file and truncate
 * it to zero size.
 */
public abstract class LockedFileWatcher extends TimerTask
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final File m_file;

    /**
     * Creates a new watcher for the specified file.
     * 
     * @param file The file to watch.
     */
    public LockedFileWatcher(final File file)
        {
        this.m_file = file;
        }

    @Override
    public final void run()
        {
        if (this.m_file.length() != 0L)
            {
            final LockedFileRunner runner = new LockedFileRunner()
                {
                public void callWithLock(final FileChannel fc)
                    {
                    onFileChange(fc);
                    }
                };
                
            FileLockUtils.callWithLock(this.m_file, runner, true);
            }
        }

    protected abstract void onFileChange(final FileChannel fc);
    }