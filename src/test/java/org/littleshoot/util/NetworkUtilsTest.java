package org.littleshoot.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.net.InetAddress;

import org.junit.Test;


public class NetworkUtilsTest
    {

    @Test public void testLocalHost() throws Exception
        {
        // This will often include the computer name.  This test just makes
        // sure including the computer name does not affect equality versus
        // just the raw IP.
        final InetAddress ia1 = NetworkUtils.getLocalHost();
        final InetAddress ia2 = InetAddress.getByName(ia1.getHostAddress());
        
        assertEquals(ia1, ia2);
        }
    
    @Test public void testPublicAddress() throws Exception
        {
        final InetAddress ec21 = InetAddress.getByName("10.254.3.3");
        final InetAddress ec22 = InetAddress.getByName("10.253.3.3");
        assertFalse(NetworkUtils.isPublicAddress(ec21));
        assertFalse(NetworkUtils.isPublicAddress(ec22));
        }
    }
