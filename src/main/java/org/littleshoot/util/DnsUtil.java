package org.littleshoot.util;
import java.util.Hashtable;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for making DNS queries.
 */
public class DnsUtil {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final InitialDirContext m_context;


    public DnsUtil() throws NamingException 
        {
        final Hashtable<String,String> env = new Hashtable<String,String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY,
            "com.sun.jndi.dns.DnsContextFactory");
        env.put("com.sun.jndi.dns.timeout.initial", "2000");
        env.put("com.sun.jndi.dns.timeout.retries", "3");
        m_context = new InitialDirContext(env);
        m_log.warn("Provider URL: {}", 
            m_context.getEnvironment().get(Context.PROVIDER_URL));
        }

    public Attributes getARecords(final String name) throws NamingException 
        {
        return m_context.getAttributes(name, new String[]{"A"});
        }

    public Attributes getPtrRecords(final String name) throws NamingException 
        {
        return m_context.getAttributes(name, new String[]{"PTR"});
        }

    public Attributes getCnamRecords(final String name) throws NamingException 
        {
        return m_context.getAttributes(name, new String[]{"CNAME"});
        }
    
    public Attributes getMxRecords(final String name) throws NamingException 
        {
        return m_context.getAttributes(name, new String[]{"MX"});
        }

    public Attributes getSrvRecords(final String name) throws NamingException 
        {
        return m_context.getAttributes(name, new String[]{"SRV"});
        }

    public Attributes getSoaRecords(final String name) throws NamingException 
        {
        return m_context.getAttributes(name, new String[]{"SOA"});
        }

    public Attributes getNsRecords(final String name) throws NamingException  
        {
        return m_context.getAttributes(name, new String[]{"NS"});
        }

    public NamingEnumeration<Binding> getHostfile(final String name) 
        throws NamingException 
        {
        return m_context.listBindings(name);
        }
    
    }
