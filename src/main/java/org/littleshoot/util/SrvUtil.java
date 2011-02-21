package org.littleshoot.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;

public interface SrvUtil
    {

    InetSocketAddress getAddress(String lookupName) throws IOException;

    Collection<InetSocketAddress> getAddresses(String lookupName) 
        throws IOException;

    }
