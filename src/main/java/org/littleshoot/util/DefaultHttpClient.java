package org.littleshoot.util;

import java.io.IOException;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.params.HttpClientParams;

/**
 * Default HTTP client class that simply resets HTTP protocol handlers to
 * their defaults.  The entire reason for this is to override the effects
 * of third party libraries setting global protocol handlers we don't want
 * to use.
 */
public interface DefaultHttpClient {
    
    HttpMethod get(final String url) throws HttpException, IOException;
    
    HttpMethod post(final String url) throws HttpException, IOException;
    
    int executeMethod(HttpMethod method) throws HttpException, IOException;

    HttpConnectionManager getHttpConnectionManager();

    HttpClientParams getParams();

    HttpState getState();

    HostConfiguration getHostConfiguration();

}
