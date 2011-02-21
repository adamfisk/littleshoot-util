package org.littleshoot.util;


/**
 * Specialized Thread subclass that's a daemon thread by default.
 */
public class DaemonThread extends Thread
    {
   
    /**
     * Creates a new daemon thread.
     */
    public DaemonThread()
        {
        super();
        setDaemon(true);
        }
    
    /**
     * Creates a new daemon thread.
     * 
     * @param name The name of the thread.
     */
    public DaemonThread(final String name)
        {
        super(name);
        setDaemon(true);
        }
    
    /**
     * Creates a new daemon thread.
     * 
     * @param runner The runnable to run on this thread.
     */
    public DaemonThread(final Runnable runner)
        {
        super(runner);
        setDaemon(true);
        }
    
    /**
     * Creates a new daemon thread.
     * 
     * @param runner The runnable to run on this thread.
     * @param name The name of the thread.
     */
    public DaemonThread(final Runnable runner, final String name)
        {
        super(runner, name);
        setDaemon(true);
        }
    }
