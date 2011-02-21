package org.littleshoot.util;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;


/**
 * This class provides static functions to load/store the files.
 */
public final class FileUtils 
    {

    /**
     * Make sure this class can't be constructed.
     */
    private FileUtils() 
        {
        // Can't construct this class.
        }

    /**
     * Utility method for checking whether or not the given file has an 
     * extension.
     * 
     * @param file The file to check for an extension on.
     * @return <code>true</code> if the given file has an extension, otherwise
     * <code>false</code>
     */
    public static boolean hasFileExtension(final File file)
        {
        if(file.isDirectory()) 
            {
            return false;
            }
        return hasFileExtension(file.getName());
        }
    
    /**
     * Utility method for checking whether or not the given file name has an 
     * extension.
     * 
     * @param name The file name to check for an extension on.
     * @return <code>true</code> if the given file name has an extension, 
     * otherwise <code>false</code>
     */
    public static boolean hasFileExtension(final String name)
        {
        return StringUtils.isNotBlank(FilenameUtils.getExtension(name));
        }
    
    
    /**
     * Several arrays of illegal characters on various operating systems.
     * Used by convertFileName
     */
    private static final char[] ILLEGAL_CHARS_ANY_OS = 
        {
        '/', '\n', '\r', '\t', '\0', '\f' 
        };
    private static final char[] ILLEGAL_CHARS_UNIX = 
        {
        '`'
        };
    
    private static final char[] ILLEGAL_CHARS_WINDOWS = 
        { 
        '?', '*', '\\', '<', '>', '|', '\"', ':'
        };

    /** 
     * Replaces OS specific illegal characters from any filename with '_', 
     * including ( / \n \r \t ) on all operating systems, ( ? * \  < > | " ) 
     * on Windows, ( ` ) on unix.
     *
     * @param name the filename to check for illegal characters
     * @return String containing the cleaned filename
     */
    public static String removeIllegalCharsFromFileName(String name)
        {

        // if the name is too long, reduce it. We don't go all the way
        // up to 256 because we don't know how long the directory name is
        // We want to keep the extension, though.
        if (name.length() > 180)
            {
            int extStart = name.lastIndexOf('.');
            if (extStart == -1)
                { // no extension, weird, but possible
                name = name.substring(0, 180);
                }
            else
                {
                // if extension is greater than 11, we concat it.
                // ( 11 = '.' + 10 extension characters )
                int extLength = name.length() - extStart;
                int extEnd = extLength > 11 ? extStart + 11 : name.length();
                name = name.substring(0, 180 - extLength)
                    + name.substring(extStart, extEnd);
                }
            }
        
        for (int i = 0; i < ILLEGAL_CHARS_ANY_OS.length; i++)
            name = name.replace(ILLEGAL_CHARS_ANY_OS[i], '_');

        if (SystemUtils.IS_OS_WINDOWS)
            {
            for (int i = 0; i < ILLEGAL_CHARS_WINDOWS.length; i++)
                name = name.replace(ILLEGAL_CHARS_WINDOWS[i], '_');
            }
        else if (SystemUtils.IS_OS_UNIX)
            {
            for (int i = 0; i < ILLEGAL_CHARS_UNIX.length; i++)
                name = name.replace(ILLEGAL_CHARS_UNIX[i], '_');
            }
        return name;
        }
    }
