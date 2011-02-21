package org.littleshoot.util;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class that uses a control lock file to check permission for reading
 * a data file.  Useful for IPC and such.
 */
public class LockedFileProcessorImpl implements LockedFileProcessor
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final LockedFileReader m_reader;
    private final File m_lockFile;
    private final File m_dataFile;
    
    /**
     * Creates a new locked file processor.
     * 
     * @param reader The reader class.
     * @param lockFile The lock file to lock.
     * @param dataFile The data file to pass to the reader once we've obtained
     * the lock.
     */
    public LockedFileProcessorImpl(final LockedFileReader reader,
        final File lockFile, final File dataFile) 
        {
        this.m_reader = reader;
        this.m_lockFile = lockFile;
        this.m_dataFile = dataFile;
        }

    public void processFile()
        {
        
        final TimerTask tt = new LockedFileWatcher(this.m_dataFile)
            {
            
            @Override
            protected void onFileChange(final FileChannel fc)
                {
                // NOTE: The timer class has automatically obtained a 
                // read and write lock on the file at this point.
                readFileData(fc);
                }
            };
            
        final Timer timer = new Timer("Lock-File-Timer-Thread", true);
        
        timer.schedule(tt, 0, 800);
        }

    private void readFileData(final FileChannel dataFileChannel)
        {
        final LockedFileRunner runner = new LockedFileRunner() 
            {
            public void callWithLock(final FileChannel lockFileChannel)
                {
                processDataFile(dataFileChannel);
                }
            };
        
        if (this.m_lockFile.length() > 0) 
            {
            m_log.error("The lock file has data!");
            }
        FileLockUtils.callWithLock(this.m_lockFile, runner, false);
        }

    private void processDataFile(final FileChannel dataFileChannel)
        {
        try
            {
            m_reader.readFile(dataFileChannel);
            }
        catch (final IOException e)
            {
            m_log.warn("Exception reading file", e);
            }
        }
    }
