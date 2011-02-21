package org.littleshoot.util;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;


public class DnsUtilTest
    {

    @Test public void testSrv() throws Exception
        {
        //if (true) return;
        final DnsUtil util = new DnsUtil();
        final Attributes attr = util.getSrvRecords("_stun._udp.littleshoot.org");
        final NamingEnumeration<String> ids = attr.getIDs();
        while(ids.hasMoreElements())
            {
            System.out.println(ids.nextElement());
            }
        
        final NamingEnumeration<? extends Attribute> elems = attr.getAll();
        while (elems.hasMoreElements())
            {
            System.out.println(elems.nextElement());
            }
        final Attribute srv = attr.get("SRV");
        System.out.println(srv);
        final Object elem = srv.get(0);
        System.out.println(elem);
        
        final NamingEnumeration<?> all = srv.getAll();
        while (all.hasMoreElements())
            {
            System.out.println(all.next());
            }
        //System.out.println(attr.);
        
        final String srvStr = elem.toString();
        final String fullAddress = StringUtils.substringAfterLast(srvStr, " ");
        final String address;
        if (fullAddress.endsWith("."))
            {
            address = fullAddress.substring(0, fullAddress.length() -1);
            }
        else
            {
            address = fullAddress;
            }
        
        System.out.println(address);
        //final String address = StringUtils.substringBeforeLast(fullAddress, ".");
        }
    }
