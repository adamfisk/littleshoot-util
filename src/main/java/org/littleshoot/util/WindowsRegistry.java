package org.littleshoot.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for reading from the registry.
 */
public class WindowsRegistry {

    private static final Logger LOG = 
        LoggerFactory.getLogger(WindowsRegistry.class);
    
    /**
     * Reads the value of a registry key.
     * 
     * @param key The registry key to query.
     * @param valueName Name of the registry value.
     * @return 
     * @return registry value or the empty string if not found.
     */
    public static final int write(final String key, 
        final String valueName, final String valueData) {
        
        final ProcessBuilder pb = 
            new ProcessBuilder("reg", "add", "\""+ key + "\"", "/v", 
                valueName, "/d", valueData);
        try {
            final Process proc = pb.start();
            return proc.waitFor();
        } catch (IOException e) {
            LOG.error("Error writing to registry", e);
        } catch (InterruptedException e) {
            LOG.error("Error writing to registry", e);
        }
        return -1;
    }
    
    /**
     * Reads the value of a registry key.
     * 
     * @param key The registry key to query.
     * @param valueName Name of the registry value.
     * @return registry value or the empty string if not found.
     */
    public static final String read(final String key, 
        final String valueName) {
        
        try {
            final Process process = Runtime.getRuntime().exec("reg query " + 
                "\""+ key + "\" /v " + valueName);
            
            final InputStream is = process.getInputStream();
            final StringWriter sw = new StringWriter();;

            final Runnable runner = new Runnable() {

                public void run() {
                    try {
                        int c;
                        while ((c = is.read()) != -1) {
                            sw.write(c);
                        }
                    }
                    catch (final IOException e) { 
                        LOG.error("Error reading reg with key '"+key+
                            "' and val '"+ valueName+"'", e);
                    }
                }
                
            };

            final Thread t = new Thread(runner, "Registry-Reading-Thread");
            t.setDaemon(true);
            t.start();
            process.waitFor();
            t.join();
            final String output = sw.toString();
            
            // This seems like slight overkill, but we want to handle generic
            // whitespace separators to accommodate OS-specific differences.
            final Scanner scan = new Scanner(output);
            String lastValue = "";
            while (scan.hasNext()) {
                lastValue = scan.next();
            }
            return lastValue;
        } catch (final IOException e) {
            LOG.error("Error reading reg with key '"+key+"' and val '"+
                valueName+"'", e);
            return "";
        } catch (final InterruptedException e) {
            LOG.error("Error reading reg with key '"+key+"' and val '"+
                valueName+"'", e);
            return "";
        }
    }

    /*
    public static void main(String[] args) {
        // Sample usage
        String value = WindowsRegistry.read(
            "HKCR\\Software\\Microsoft\\Windows\\CurrentVersion\\"  + 
            "Internet Settings", "ProxyServer");
        System.out.println("'"+value+"'");
        
        int result = WindowsRegistry.write(
            "HKCR\\Software\\Microsoft\\Windows\\CurrentVersion\\"  + 
            "Internet Settings", "ProxyEnable", "1");
        System.out.println(result);
    }
    */
}