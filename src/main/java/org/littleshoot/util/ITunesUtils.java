package org.littleshoot.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities for iTunes.
 */
public class ITunesUtils
    {

    private static final Logger LOG = 
        LoggerFactory.getLogger(ITunesUtils.class);
    
    private ITunesUtils() {}

    /**
     * Adds the specified audio file to iTunes if we're on OSX.
     * 
     * @param file The file to add.
     * @return <code>true</code> if the audio file was added to iTunes, 
     * otherwise <code>false</code>.
     * @throws IOException If there's an error getting the canonical file name. 
     */
    public static boolean addAudioFile(final File file) throws IOException
        {
        if (!SystemUtils.IS_OS_MAC_OSX) 
            {
            LOG.debug("Not on OSX, not adding to iTunes");
            return false;
            }
        
        if (!isSupported(file))
            {
            LOG.debug("iTunes does not support this file type: {}", file);
            return false;
            }
        
        //final Runnable runner = new ExecOsaScript(file.getCanonicalFile());
        //final Thread thread = new DaemonThread(runner, "iTunes-Adding-Thread");
        //thread.start();
        
        Runtime.getRuntime().exec(createOSAScriptCommand(file));
        return true;
        }
    
    /**
     * Returns true if the extension of name is a supported file type.
     */
    private static boolean isSupported(final File file)
        {
        final String extension = FilenameUtils.getExtension(file.getName());
        if (StringUtils.isBlank(extension))
            {
            return false;
            }
        
        final String[] types = 
            {
            "mp3", "aif", "aiff", "wav", "mp2", "mp4",
            "aac", "mid", "m4a", "m4p", "ogg"
            };
        if (ArrayUtils.contains(types, extension.toLowerCase()))
            {
            return true;
            }
        return false;
        }

    /**
     * Constructs and returns a osascript command.
     */
    private static String[] createOSAScriptCommand(final File file)
        {
        final String path = file.getAbsolutePath();
        final String playlist = "LittleShoot";

        final String[] command = new String[]
            {
            "osascript",
            "-e", "tell application \"Finder\"",
            "-e",     "set hfsFile to (POSIX file \"" + path + "\")",
            "-e",     "set thePlaylist to \"" + playlist + "\"",
            "-e",     "tell application \"iTunes\"",
                    // "-e", "activate", // launch and bring to front
            "-e",         "launch", // launch in background
            "-e",         "if not (exists playlist thePlaylist) then", 
            "-e",             "set thisPlaylist to make new playlist", 
            "-e",             "set name of thisPlaylist to thePlaylist", 
            "-e",         "end if",
            "-e",         "add hfsFile to playlist thePlaylist", 
            "-e",     "end tell", 
            "-e", "end tell" 
            };

        return command;
        }

    /**
     * Executes the osascript CLI command
     */
    private static final class ExecOsaScript implements Runnable
        {
        /**
         * The file to add.
         */
        private final File m_file;
        
        private ExecOsaScript(final File file)
            {
            this.m_file = file;
            }

        /**
         * Runs the script command
         */
        public void run()
            {
            try
                {
                Runtime.getRuntime().exec(createOSAScriptCommand(m_file));
                }
            catch (final IOException err)
                {
                LOG.warn("Error adding to iTunes", err);
                }
            }
        }
    }
