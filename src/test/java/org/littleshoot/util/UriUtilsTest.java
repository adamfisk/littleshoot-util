package org.littleshoot.util;

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

public class UriUtilsTest
    {

    @Test public void testUris() throws Exception 
        {
        final Map<String, String> params = new LinkedHashMap<String, String>();
        params.put("fromSiteListener", "true");
        params.put("appVersion", String.valueOf(0.80));
        
        final String uri =
            UriUtils.newUrl(ShootConstants.SERVER_URL, params);
        
        //final String uri = "http://www.littleshoot.org/?fromSiteListener=true";
        //final Process p = UriUtils.openUri(uri);
        //assertNotNull(p);
        
        
        String file = "file:///Users/adamfisk";
        //final Process p = UriUtils.openUri(file);
        }
    
    @Test public void testStripHost() throws Exception 
        {
        final String uri = "http://test.com/pathIsHere";
        final String noHostUri = UriUtils.stripHost(uri);
        assertEquals("/pathIsHere", noHostUri);
        }
    }
