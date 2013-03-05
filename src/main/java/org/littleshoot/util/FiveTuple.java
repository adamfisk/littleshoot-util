package org.littleshoot.util;

import java.net.InetSocketAddress;

/**
 * This is a 5 tuple consisting of a local address and port, a remote 
 * address and port, and the network protocol in use.
 */
public class FiveTuple {

    private final InetSocketAddress local;
    private final InetSocketAddress remote;
    private final Protocol protocol;

    public enum Protocol {
        TCP,
        UDP
    }
    
    public FiveTuple(final InetSocketAddress local, 
        final InetSocketAddress remote, final Protocol protocol) {
        this.local = local;
        this.remote = remote;
        this.protocol = protocol;
        
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public InetSocketAddress getRemote() {
        return remote;
    }

    public InetSocketAddress getLocal() {
        return local;
    }

    @Override
    public String toString() {
        return "FiveTuple [local=" + local + ", remote=" + remote
                + ", protocol=" + protocol + "]";
    }
}
