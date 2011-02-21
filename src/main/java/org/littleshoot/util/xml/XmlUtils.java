package org.littleshoot.util.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.littleshoot.util.IoExceptionWithCause;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * XML utilities class.
 */
public final class XmlUtils
    {
    
    private static final Logger LOG = LoggerFactory.getLogger(XmlUtils.class);
    
    private static final XPathFactory s_xPathFactory = 
        XPathFactory.newInstance();
    private static final XPath s_path = s_xPathFactory.newXPath();

    /**
     * Replaces the text value of a node in a document with the specified value
     * using XPath.
     * 
     * @param doc The document to modify.
     * @param xPath The XPath string to locate the node to modify.  This
     * should point to the {@link Node} containing a {@link Text} node as its
     * first child.
     * @param newValue The new text value to place in the node.
     */
    public static void replaceNodeValue(final Document doc, final String xPath, 
        final String newValue)
        {
        final Text textNode = doc.createTextNode(newValue);
        try
            {
            final Node node = (Node) s_path.evaluate(xPath, doc, 
                XPathConstants.NODE);
            if (LOG.isDebugEnabled())
                {
                LOG.debug("Found node: "+node.getNodeName());
                }
            if (node == null)
                {
                LOG.warn("Could not find xPath: "+xPath);
                return;
                }
            node.replaceChild(textNode, node.getFirstChild());
            }
        catch (final XPathExpressionException e)
            {
            LOG.error("Bad XPath: "+xPath, e);
            } 
        }

    /**
     * Prints the XML {@link Document} to standard out with handy indentation.
     * 
     * @param doc The {@link Document} to print.
     */
    public static void printDoc(final Document doc)
        {
        try
            {
            final Transformer trans = 
                TransformerFactory.newInstance().newTransformer();
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.transform(new DOMSource(doc), new StreamResult(System.out));
            }
        catch (final TransformerConfigurationException e)
            {
            LOG.error("Could not configure transformer", e);
            }
        catch (final TransformerFactoryConfigurationError e)
            {
            LOG.error("Could not configure transformer factory", e);
            }
        catch (final TransformerException e)
            {
            LOG.error("Error running transformation", e);
            }
        }
    
    /**
     * Returns the XML {@link Document} as a readable string.
     * 
     * @param doc The {@link Document} to convert to a string.
     * @return The returned {@link String}.
     */
    public static String toString(final Document doc)
        {
        try
            {
            final Transformer trans = 
                TransformerFactory.newInstance().newTransformer();
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            final StringWriter sw = new StringWriter();
            trans.transform(new DOMSource(doc), new StreamResult(sw));
            return sw.toString();
            }
        catch (final TransformerConfigurationException e)
            {
            LOG.error("Could not configure transformer", e);
            }
        catch (final TransformerFactoryConfigurationError e)
            {
            LOG.error("Could not configure transformer factory", e);
            }
        catch (final TransformerException e)
            {
            LOG.error("Error running transformation", e);
            }
        return StringUtils.EMPTY;
        }
    
    /**
     * Empty list of XML nodes.
     */
    public static final NodeList EMPTY_NODE_LIST = new NodeList()
        {
        public Node item(final int index)
            {
            return null;
            }

        public int getLength()
            {
            return 0;
            }
        };

    public static Document toDoc(final String str) throws IOException, 
        SAXException
        {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        final DocumentBuilder db;
        try
            {
            db = dbf.newDocumentBuilder();
            }
        catch (final ParserConfigurationException e)
            {
            // This should never happen.
            LOG.error("Parser error?", e);
            throw new IoExceptionWithCause("Parser error??", e);
            }
        return db.parse(new InputSource(new StringReader(str)));
        }

    public static Document toDoc(final InputStream is) throws IOException, 
        SAXException
        {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        final DocumentBuilder db;
        try
            {
            db = dbf.newDocumentBuilder();
            }
        catch (final ParserConfigurationException e)
            {
            // This should never happen.
            LOG.error("Parser error?", e);
            throw new IoExceptionWithCause("Parser error??", e);
            }
        return db.parse(is);
        }

    public static void printDoc(final String xml)
        {
        try
            {
            final Document doc = toDoc(xml);
            printDoc(doc);
            }
        catch (IOException e)
            {
            LOG.warn("Could not print", e);
            }
        catch (SAXException e)
            {
            LOG.warn("Could not print", e);
            }
        }
    }
