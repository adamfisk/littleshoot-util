package org.littleshoot.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.apache.commons.io.IOExceptionWithCause;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility method for accessing DNS SRV records.
 */
public class SrvUtilImpl implements SrvUtil
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());

    /**
     * Creates a new SRV utility class.
     */
    public SrvUtilImpl()
        {
        // Empty for now.
        }
    
    public Collection<InetSocketAddress> getAddresses(final String lookupName) 
        throws IOException
        {
        //return getDnsJavaAddress(lookupName);
        return getJndiAddresses(lookupName);
        }
    
    /*
    private Collection<InetSocketAddress> getDnsJavaAddress(
        final String lookupName) throws IOExceptionWithCause
        {
        final Record[] records;
        try
            {
            records = new Lookup(lookupName, Type.SRV).run();
            }
        catch (final TextParseException e)
            {
            m_log.error("Could not perform lookup", e);
            throw new IOExceptionWithCause("Could not perform lookup", e);
            }
        final Collection<InetSocketAddress> addresses = 
            new ArrayList<InetSocketAddress>(records.length);
        for (int i = 0; i < records.length; i++) 
            {
            final SRVRecord srv = (SRVRecord) records[i];
            System.out.println("SRV: "+srv);
            final Name target = srv.getTarget();
            System.out.println("TARGET: "+target);
            final Name name = srv.getName();
            System.out.println("NAME: "+name);
            }
        return addresses;
        }
        */

    private Collection<InetSocketAddress> getJndiAddresses(
        final String lookupName) throws IOException
        {
        final DnsUtil util;
        try
            {
            util = new DnsUtil();
            }
        catch (final NamingException e)
            {
            m_log.error("Could not create DNS util...", e);
            throw new IOExceptionWithCause("Could not create DNS util?", e);
            }
        final Attributes attr;
        try
            {
            attr = util.getSrvRecords(lookupName);
            }
        catch (final NamingException e)
            {
            m_log.error("Invalid SRV name?", e);
            throw new IOExceptionWithCause("Invalid SRV name?", e);
            }

        final Attribute srv = attr.get("SRV");
        final int size = srv.size();
        
        final List<InetSocketAddress> addresses = 
            new LinkedList<InetSocketAddress>();
        for (int i = 0; i < size; i++)
            {
            final Object elem;
            try
                {
                elem = srv.get(i);
                }
            catch (final NamingException e)
                {
                m_log.error("Invalid SRV name?", e);
                throw new IOExceptionWithCause("Invalid SRV name?", e);
                }
            
            final String srvStr = elem.toString();
            final Scanner scan = new Scanner(srvStr);
            
            // Not sure what this int is.
            scan.nextInt();
            // Or this one.
            scan.nextInt();
            final int port = scan.nextInt();
            
            
            final String fullAddress = scan.next();
            final String address;
            if (fullAddress.endsWith("."))
                {
                address = fullAddress.substring(0, fullAddress.length() -1);
                }
            else
                {
                address = fullAddress;
                }
            final InetSocketAddress isa = new InetSocketAddress(address, port);
            addresses.add(isa);
            }
        
        Collections.shuffle(addresses);
        return new HashSet<InetSocketAddress>(addresses);
        }

    public InetSocketAddress getAddress(final String lookupName) 
        throws IOException
        {
        final Collection<InetSocketAddress> addresses = getAddresses(lookupName);
        if (addresses.isEmpty())
            {
            return null;
            }
        
        return addresses.iterator().next();
        }

    }
