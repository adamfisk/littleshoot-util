package org.littleshoot.util;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for keeping track of <code>HttpClient</code> connections created.  
 * This provides access to the underlying <code>HttpConnection</code> 
 * instances for things such as closing the connection.  This also allows the
 * setting of general properties for connections we create.
 */
public class DefaultHttpClientImpl implements DefaultHttpClient
    {
    
    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());

    private final HttpClient m_httpClient; 
    
    /**
     * Creates a new manager using a custom {@link HttpConnectionManager}
     * instance.
     * 
     * @param connectionManager The {@link HttpConnectionManager} to use.
     */
    public DefaultHttpClientImpl(final HttpConnectionManager connectionManager)
        {
        m_httpClient = new HttpClient(connectionManager);
        this.m_httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(40 * 1000);
        final HttpClientParams params = this.m_httpClient.getParams();
        params.setSoTimeout(60 * 1000);
        
        final String versionString = 
            System.getProperty("org.lastbamboo.client.version", "0.00");
        params.setParameter("http.useragent", "LittleShoot/"+versionString);
        }
    
    /**
     * Creates a new manager.
     */
    public DefaultHttpClientImpl()
        {
        this(new ResettingMultiThreadedHttpConnectionManager());
        }

    public HttpMethod get(final String url) throws HttpException, IOException
        {
        final GetMethod method = new GetMethod(url);
        return execute(method);
        }

    public HttpMethod post(final String url) throws HttpException, IOException
        {
        final PostMethod method = new PostMethod(url);
        return execute(method);
        }

    public int executeMethod(final HttpMethod method) throws HttpException, 
        IOException
        {
        execute(method);
        return method.getStatusCode();
        }
        
    private HttpMethod execute(final HttpMethod method) 
        throws HttpException, IOException
        {
        this.m_httpClient.executeMethod(method);
        return method;
        }

    public HttpConnectionManager getHttpConnectionManager()
        {
        return this.m_httpClient.getHttpConnectionManager();
        }

    public HttpClientParams getParams()
        {
        return this.m_httpClient.getParams();
        }

    public HttpState getState()
        {
        return this.m_httpClient.getState();
        }
    }
