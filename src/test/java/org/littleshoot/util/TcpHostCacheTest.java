package org.littleshoot.util;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;


public class TcpHostCacheTest
    {
    
    @Test public void testTcpConnect() throws Exception 
        {
        final DefaultHttpClient httpClient = 
            new DefaultHttpClientImpl();
        final Map<String, String> params = new HashMap<String, String>();
        params.put("hostfile", "1");
        params.put("client", "LIME");
        params.put("version", "4.18.8");
        
        final String url = 
            UriUtils.newUrl("http://secondary.udp-host-cache.com:8080/gwc/", params);
        final GetMethod get = new GetMethod(url);
        get.addRequestHeader("Cache-Control", "no-cache");
        
        httpClient.executeMethod(get);

        final String response = get.getResponseBodyAsString();
        assertTrue(StringUtils.isNotBlank(response));
        }
    }
