package org.littleshoot.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.apache.commons.io.IOExceptionWithCause;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Candidate provider that uses native commands to lookup SRV records.
 */
public class DnsSrvCandidateProvider 
    implements CandidateProvider<InetSocketAddress>
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final CandidateProvider<InetSocketAddress> m_osSpecificProvider;

    private final String m_lookupName;
    
    private static final Pattern NSLOOKUP_PATTERN = 
        Pattern.compile("\\s+port\\s+=\\s+(\\S+)\\s+svr hostname\\s+=\\s+(\\S+)");

    /**
     * Creates a new DNS candidate provider with the specified SRV name to 
     * lookup.
     *  
     * @param lookupName The SRV name to lookup.
     */
    public DnsSrvCandidateProvider(final String lookupName)
        {
        m_log.info("Existing nameservers: "+ 
            System.getProperty("sun.net.spi.nameservice.nameservers"));
        System.setProperty("sun.net.spi.nameservice.nameservers", "8.8.8.8,8.8.4.4");
        if (SystemUtils.IS_OS_WINDOWS)
            {
            final String data = 
                nativeData("nslookup", "-type=srv", lookupName, "8.8.8.8");
            m_osSpecificProvider = 
                new OsCandidateProvider(data, NSLOOKUP_PATTERN);
            }
        else
            {
            final String data = nativeData("dig", "@8.8.8.8", "SRV", lookupName);
            final Pattern digMatcher = 
                Pattern.compile(lookupName+".\\s+\\d+\\s+IN\\s+SRV\\s+\\d+\\s+\\d+\\s+(\\d+)\\s+([\\w\\.]+)\\.");
            m_osSpecificProvider = new OsCandidateProvider(data, digMatcher);
            }
        this.m_lookupName = lookupName;
        }
    
    private String nativeData(final String... commands) 
        {
        final ProcessBuilder pb = new ProcessBuilder(commands);
        try
            {
            final Process process = pb.start();
            final InputStream is = process.getInputStream();
            final String data = IOUtils.toString(is);
            m_log.info("Got data from native call: {}", data);
            return data;
            }
        catch (final IOException e)
            {
            m_log.error("Error running commands: "+Arrays.asList(commands), e);
            return "";
            }
        }

    public InetSocketAddress getCandidate()
        {
        final InetSocketAddress candidate = 
            this.m_osSpecificProvider.getCandidate();
        if (candidate == null) 
            {
            try 
                {
                final Collection<InetSocketAddress> addresses = 
                    getJndiAddresses(this.m_lookupName);
                if (addresses.isEmpty())
                    {
                    m_log.error("Addresses are empty!");
                    return null;
                    }
                return addresses.iterator().next();
                } 
            catch (final IOException e) 
                {
                m_log.error("Error looking up JNDI addresses", e);
                return null;
                }
            }
        else 
            {
            return candidate;
            }
        }

    public Collection<InetSocketAddress> getCandidates()
        {
        final Collection<InetSocketAddress> candidates =
            this.m_osSpecificProvider.getCandidates();
        if (candidates == null || candidates.isEmpty())
            {
            try 
                {
                final Collection<InetSocketAddress> addresses = 
                    getJndiAddresses(this.m_lookupName);
                return addresses;
                } 
            catch (final IOException e) 
                {
                m_log.error("Error looking up JNDI addresses", e);
                return Collections.emptyList();
                }
            }
        else 
            {
            return candidates;
            }
        }
    
    private static final class OsCandidateProvider 
        implements CandidateProvider<InetSocketAddress>
        {
        private final Collection<InetSocketAddress> candidates =
            new LinkedList<InetSocketAddress>();
        private OsCandidateProvider(final String data, final Pattern pattern)
            {
            final Matcher match = pattern.matcher(data);
            while (match.find()) 
                {
                //System.out.println("MATCH: " +match.group(0));
                //System.out.println("MATCH 1: " +match.group(1));
                //System.out.println("MATCH 2: " +match.group(2));
                final int port = Integer.parseInt(match.group(1));
                final String host = match.group(2);
                candidates.add(new InetSocketAddress(host, port));
                }
            }
    
        public InetSocketAddress getCandidate()
            {
            if (!candidates.isEmpty())
                {
                return candidates.iterator().next();
                }
            return null;
            }
    
        public Collection<InetSocketAddress> getCandidates()
            {
            return candidates;
            }
        }
    

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
    }
