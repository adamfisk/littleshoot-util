package org.littleshoot.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.OverlappingFileLockException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for dealing with file locks.
 */
public class FileLockUtils
    {
    private static final Logger LOG = 
        LoggerFactory.getLogger(FileLockUtils.class);
    
    private FileLockUtils(){}

    /**
     * Calls the specified {@link Runnable} only when a lock is obtained on
     * the specified file.
     * 
     * @param file The file to obtain a lock on.
     * @param runner The class to call when we have the lock.
     */
    public static void callWithLock(final File file, 
        final LockedFileRunner runner)
        {
        callWithLock(file, runner, false);
        }
    
    /**
     * Calls the specified {@link Runnable} only when a lock is obtained on
     * the specified file.
     * 
     * @param file The file to obtain a lock on.
     * @param runner The class to call when we have the lock.
     * @param truncate Whether or not to truncate the file when we're done --
     * useful in some file IPC scenarios.
     */
    public static void callWithLock(final File file, 
        final LockedFileRunner runner, final boolean truncate)
        {
        final RandomAccessFile raf;
        try
            {
            raf = new RandomAccessFile(file, "rw");
            }
        catch (final FileNotFoundException e)
            {
            LOG.error("Could not find torrent data file?", e);
            return;
            }
        
        final FileChannel fc = raf.getChannel();
        FileLock lock = null;
        try
            {
            lock = fc.lock();
            if (!lock.isValid())
                {
                LOG.error("Lock not valid?");
                }
            runner.callWithLock(fc);
            }
        catch (final FileNotFoundException e)
            {
            LOG.error("Could not find lock file?", e);
            }
        catch (final OverlappingFileLockException e)
            {
            LOG.debug("Overlapping file lock", e);
            }
        catch (final ClosedChannelException e)
            {
            LOG.error("Closed channel?", e);
            }
        catch (final NonWritableChannelException e)
            {
            LOG.error("Closed channel?", e);
            }
        catch (final IOException e)
            {
            LOG.error("IO Error getting lock?", e);
            }
        finally 
            {
            // If truncate is true, we set the file size to zero to keep it 
            // from growing forever.  We do this even in the case of errors, as 
            // it's unlikely we'll successfully process a file later than has
            // produced an error once.
            if (lock == null)
                {
                LOG.error("Lock file is null!!");
                }
            if (lock != null && truncate)
                {
                try
                    {
                    LOG.debug("Truncating file");
                    fc.truncate(0);
                    LOG.debug("Truncated file");
                    if (fc.size() != 0L)
                        {
                        LOG.error("Truncated file to 0 but size is: "+fc.size());
                        }
                    }
                catch (final IOException e)
                    {
                    LOG.warn("Could not truncate the file", e);
                    }
                }
            try
                {
                raf.close();
                }
            catch (final IOException e)
                {
                LOG.warn("Could not close RAF", e);
                }
            if (lock != null)
                {
                try
                    {
                    lock.release();
                    }
                catch (final IOException e)
                    {
                    LOG.debug("IOException releasing lock", e);
                    }
                }
            }
        }
    }
