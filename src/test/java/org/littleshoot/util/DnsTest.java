package org.littleshoot.util;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.junit.Test;


public class DnsTest
    {

    private static final class DigCandidateProvider implements CandidateProvider<InetSocketAddress>
        {
        
        private DigCandidateProvider(final String data)
            {
            }

        public InetSocketAddress getCandidate()
            {
            return null;
            }

        public Collection<InetSocketAddress> getCandidates()
            {
            return null;
            }
        
        }
    
    @Test public void testSrv() throws Exception 
        {
        final String[] args;
        
        if (SystemUtils.IS_OS_WINDOWS)
            {
            args = new String[] {"nslookup", "-type=srv", "_sip._tcp.example.com"};
            }
        else
            {
            args = new String[]{"dig", "SRV", "_stun._udp.littleshoot.org"};
            }
        final Runtime runtime = Runtime.getRuntime();
        final Process process = runtime.exec(args);
        final InputStream is = process.getInputStream();
        //final InputStreamReader isr = new InputStreamReader(is);
        //final BufferedReader br = new BufferedReader(isr);
        //String line;

        final String data = IOUtils.toString(is);
        System.out.println(data);
        /*
        while ((line = br.readLine()) != null) 
            {
            System.out.println(line);
            }
            */
        }
    }
