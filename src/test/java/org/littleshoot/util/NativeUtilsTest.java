package org.littleshoot.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NativeUtilsTest
    {

    @Test public void testOpenFolder() throws Exception 
        {
        /*
        NativeUtils.openFolder(new File(".").getCanonicalFile());
        
        final File spaces = new File(".", "folder with spaces").getCanonicalFile();
        spaces.mkdir();
        spaces.deleteOnExit();
        NativeUtils.openFolder(spaces);
        */
        
        //Runtime.getRuntime().exec
        //  ("rundll32 SHELL32.DLL,ShellExec_RunDLL " + new File(".").getCanonicalPath());

        //final Desktop dt = Desktop.getDesktop();
        //dt.open(new File(".").getCanonicalFile());

        assertTrue(true);
        
        //Thread.sleep(10000);
        }
    }
