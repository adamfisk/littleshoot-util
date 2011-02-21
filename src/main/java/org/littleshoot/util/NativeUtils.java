package org.littleshoot.util;

import java.awt.Image;
import java.awt.PopupMenu;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;

import org.apache.commons.io.IOExceptionWithCause;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities for native calls.
 */
public class NativeUtils
    {
    private static final Logger LOG = 
        LoggerFactory.getLogger(NativeUtils.class);
    
    /**
     * Opens the specified URI using the native file system.  Currently only
     * HTTP URIs are officially supported.
     * 
     * @param uri The URI to open.
     * @return The Process started, or <code>null</code> if there was an
     * error.
     */
    public static Process openUri(final String uri)
        {
        if (SystemUtils.IS_OS_MAC_OSX)
            {
            return openSiteMac(uri);
            }
        else if (SystemUtils.IS_OS_WINDOWS)
            {
            return openSiteWindows(uri);
            }
        
        return null;
        }
    
    private static Process openSiteMac(final String siteUrl)
        {
        return exec ("open", siteUrl);
        }
    
    private static Process openSiteWindows(final String siteUrl)
        {
        return exec ("cmd.exe", "/C", "start", siteUrl);
        }
    
    private static Process exec(final String... cmds)
        {
        final ProcessBuilder pb = new ProcessBuilder(cmds);
        try
            {
            return pb.start();
            }
        catch (final Exception e)
            {
            LOG.error("Could not open site", e);
            }
        return null;
        }

    public static void openFolder(final File folder) throws IOException 
        {
        if (!folder.isDirectory())
            {
            LOG.warn("No directory at: {}", folder);
            }
        if (SystemUtils.IS_OS_WINDOWS)
        //if (SystemUtils.isJavaVersionAtLeast(1.6f))
            {
            final Class[] argTypes = new Class[]{java.io.File.class};
            try
                {
                final Class desktopClass = Class.forName("java.awt.Desktop");
                final Object obj = 
                    desktopClass.getDeclaredMethod("getDesktop").invoke(null);
                desktopClass.getDeclaredMethod("open", argTypes).invoke(obj, 
                    folder);
                }
            catch (final Exception e)
                {
                LOG.error("Opening folder failed!!", e);
                if (SystemUtils.IS_OS_MAC_OSX)
                    {
                    exec ("open", "'"+folder.getCanonicalPath()+"'");
                    }
                throw new IOExceptionWithCause("Can't open folders on:" + 
                    SystemUtils.OS_NAME, e);
                }
            }
        else if (SystemUtils.IS_OS_MAC_OSX)
            {
            try
                {
                final String path = folder.getCanonicalPath();
                LOG.debug("Opening path: {}", path);
                final Process proc = exec ("open", path);
                //LOG.debug("Process is: " + IOUtils.toString(proc.getInputStream()));
                }
            catch (final IOException e)
                {
                LOG.error("Exception opening folder", e);
                }
            }
        else
            {
            LOG.debug("We don't know how to open folders on other OSes");
            throw new IOException("Can't open folders on:"+SystemUtils.OS_NAME);
            }
        }

    /**
     * Adds a tray icon using reflection.  This succeeds if the underlying 
     * JVM is 1.6 and supports the system tray, failing otherwise.
     * 
     * @param image The image to use for the system tray icon.
     * @param name The name of the system tray item.
     * @param popup The popup menu to display when someone clicks on the tray.
     */
    public static void addTray(final Image image, final String name, 
        final PopupMenu popup)
        {
        final Class[] trayIconArgTypes = new Class[] {
            java.awt.Image.class, 
            java.lang.String.class, 
            java.awt.PopupMenu.class
        };
        try
            {
            final Class trayIconClass = Class.forName("java.awt.TrayIcon");
            final Constructor trayIconConstructor = 
                trayIconClass.getConstructor(trayIconArgTypes);
            final Object trayIcon = 
                trayIconConstructor.newInstance(image, name, popup);
            
            final Class trayClass = Class.forName("java.awt.SystemTray");
            final Object obj = 
                trayClass.getDeclaredMethod("getSystemTray").invoke(null);
            
            final Class[] trayAddArgTypes = new Class[] {
                trayIconClass 
            };
            trayClass.getDeclaredMethod("add", trayAddArgTypes).invoke(obj, trayIcon);
            }
        catch (final Exception e)
            {
            LOG.warn("Reflection error", e);
            }
        }

    /**
     * Uses reflection to determine whether or not this operating system and
     * java version supports the system tray.
     * 
     * @return <code>true</code> if it supports the tray, otherwise 
     * <code>false</code>.
     */
    public static boolean supportsTray()
        {
        try
            {
            final Class trayClass = Class.forName("java.awt.SystemTray");
            final Boolean bool = 
                (Boolean) trayClass.getDeclaredMethod("isSupported").invoke(null);
            return bool.booleanValue();
            }
        catch (final Exception e)
            {
            LOG.warn("Reflection error", e);
            return false;
            }
        }
    }
