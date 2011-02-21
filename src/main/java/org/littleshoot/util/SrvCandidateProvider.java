package org.littleshoot.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic class for accessing "candidate" URLs for a service to use that 
 * performs lookups using DNS SRV records. These are known to occasionally fail,
 * so this class will keep trying.
 */
public class SrvCandidateProvider 
    implements CandidateProvider<InetSocketAddress> 
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());

    private final InetSocketAddress m_fallbackAddress;

    private final String m_srvAddress;

    private Collection<InetSocketAddress> m_addresses = 
        new HashSet<InetSocketAddress>();

    private SrvUtil m_srvUtil;

    private boolean m_srvSucceeded;

    /**
     * Constructs a new registrar candidate provider.
     * 
     * @param srvUtil The SRV lookup utility class.
     * @param srvAddress The address for looking up DNS SRV records.
     */
    public SrvCandidateProvider(final SrvUtil srvUtil, final String srvAddress)
        {
        this(srvUtil, srvAddress, null);
        }
    
    /**
     * Constructs a new registrar candidate provider.
     * 
     * @param srvUtil The SRV lookup utility class.
     * @param srvAddress The address for looking up DNS SRV records.
     * @param defaultAddress The address to use if SRV lookups fail. If you
     * set this to <code>null</code>, we'll just keep trying SRV lookups.
     */
    public SrvCandidateProvider(final SrvUtil srvUtil, final String srvAddress, 
        final InetSocketAddress defaultAddress)
        {
        this.m_srvUtil = srvUtil;
        this.m_srvAddress = srvAddress;

        this.m_fallbackAddress = defaultAddress;
        this.m_addresses = getCandidates();
        }
        
    public Collection<InetSocketAddress> getCandidates() 
        {
        m_log.info("Getting SRV candidates");
        if (!this.m_addresses.isEmpty())
            {
            return this.m_addresses;
            }
        else if (this.m_srvSucceeded)
            {
            // Maybe the SRV record intentionally turned off hits to the 
            // server?
            return Collections.emptyList();
            }
        m_log.info("Continuing with lookup");
        final Collection<InetSocketAddress> addresses = 
            new HashSet<InetSocketAddress>();
        
        if (StringUtils.isNotBlank(m_srvAddress))
            {
            for (int i = 0; i < 2; i++)
                {
                try 
                    {
                    final Collection<InetSocketAddress> srvAddresses = 
                        this.m_srvUtil.getAddresses(m_srvAddress);
                    m_log.info("Got SRV addresses: {}", srvAddresses);
                    addresses.addAll(srvAddresses);
                    this.m_srvSucceeded = true;
                    break;
                    } 
                catch (final IOException e) 
                    {
                    this.m_log.warn("SRV lookup error!", e);
                    }
                }
            }
        
        // If the DNS SRV lookups fail, we add the fallback address if it's 
        // specified.
        if (!this.m_srvSucceeded && m_fallbackAddress != null) 
            {   
            m_log.warn("Adding fallback address: {}", m_fallbackAddress);
            addresses.add(m_fallbackAddress);
            }
        this.m_addresses = addresses;
        return addresses;
        }
    
    public InetSocketAddress getCandidate() 
        {
        final Collection<InetSocketAddress> candidates = getCandidates();
        if (candidates.isEmpty()) return null;
        return candidates.iterator().next();
        }
    }
