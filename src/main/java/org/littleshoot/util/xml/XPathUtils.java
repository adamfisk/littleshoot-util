package org.littleshoot.util.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.littleshoot.util.DefaultHttpClient;
import org.littleshoot.util.DefaultHttpClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Implementation of XPath utilities. 
 */
public class XPathUtils 
    {

    private static final Logger LOG = 
        LoggerFactory.getLogger(XPathUtils.class);
    private final XPath m_path;
    private final Document m_doc;

    /**
     * Creates a new XPath utilities instance for the specified XPath.
     * 
     * @param path The built-in java XPath class.
     * @param doc The XML document to use XPath on.
     */
    private XPathUtils(final XPath path, final Document doc)
        {
        m_path = path;
        m_doc = doc;
        }
    
    /**
     * Creates a new {@link XPathUtils} instance from the data at the specified 
     * HTTP url.
     * 
     * @param url The URL to load XML data from.
     * @return An {@link XPathUtils} instance for the data at the given URL.
     */
    public static XPathUtils newXPath(final URL url)
        {
        final DefaultHttpClient client = new DefaultHttpClientImpl();
        final GetMethod method = new GetMethod(url.toExternalForm());
        try
            {
            final int responseCode = client.executeMethod(method);
            if (responseCode != HttpStatus.SC_OK)
                {
                LOG.warn("Unexpected response code: "+responseCode);
                }
            else
                {
                final InputStream is = method.getResponseBodyAsStream();
                return XPathUtils.newXPath(is);
                }
            }
        catch (final HttpException e)
            {
            LOG.warn("Could not handle XPath", e);
            }
        catch (final IOException e)
            {
            LOG.warn("Could not handle XPath", e);
            }
        catch (final SAXException e)
            {
            LOG.warn("Could not handle XPath", e);
            }
        finally
            {
            method.releaseConnection();
            }
        return null;
        }
  
    /**
     * Creates a new {@link XPathUtils} instance.
     * 
     * @param str The string with XML data.
     * @return A new {@link XPathUtils} instance.
     * @throws SAXException If there's a SAX error in the XML.
     * @throws IOException If there's an IO error reading the stream.
     */
    public static XPathUtils newXPath(final String str) 
        throws SAXException, IOException
        {
        final XPathFactory xpfactory = XPathFactory.newInstance();
        final XPath xPath = xpfactory.newXPath();
        final Document doc = XmlUtils.toDoc(str);
        return new XPathUtils(xPath, doc);
        }
    
    /**
     * Creates a new {@link XPathUtils} instance.
     * 
     * @param is The {@link InputStream} with XML data.
     * @return A new {@link XPathUtils} instance.
     * @throws SAXException If there's a SAX error in the XML.
     * @throws IOException If there's an IO error reading the stream.
     */
    public static XPathUtils newXPath(final InputStream is) 
        throws SAXException, IOException
        {
        final XPathFactory xpfactory = XPathFactory.newInstance();
        final XPath xPath = xpfactory.newXPath();
        final Document doc = XmlUtils.toDoc(is);
        return new XPathUtils(xPath, doc);
        }

    /**
     * Creates a new {@link XPathUtils} instance.
     * 
     * @param doc The XML data.
     * @return A new {@link XPathUtils} instance.
     */
    public static XPathUtils newXPath(final Document doc)
        {
        final XPathFactory xpfactory = XPathFactory.newInstance();
        final XPath xPath = xpfactory.newXPath();
        return new XPathUtils(xPath, doc);
        }

    public int getInt(final String xPath) throws XPathExpressionException
        {
        return ((Double) this.m_path.evaluate(xPath, this.m_doc, 
            XPathConstants.NUMBER)).intValue();
        }

    public String getString(final String xPath) throws XPathExpressionException
        {
        return (String) this.m_path.evaluate(xPath, this.m_doc, 
            XPathConstants.STRING);
        }

    public NodeList getNodes(final String xPath) throws XPathExpressionException
        {
        return (NodeList) this.m_path.evaluate(xPath, this.m_doc, 
            XPathConstants.NODESET);
        }

    public URL getUrl(final String xPath) throws MalformedURLException, 
        XPathExpressionException
        {
        final String url = getString(xPath);
        return new URL(url);
        }

    public Node getNode(final String xPath) throws XPathExpressionException
        {
        return (Node) this.m_path.evaluate(xPath, this.m_doc, 
            XPathConstants.NODE);
        }

    }
