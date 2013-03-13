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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((local == null) ? 0 : local.hashCode());
        result = prime * result
                + ((protocol == null) ? 0 : protocol.hashCode());
        result = prime * result + ((remote == null) ? 0 : remote.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FiveTuple other = (FiveTuple) obj;
        if (local == null) {
            if (other.local != null)
                return false;
        } else if (!local.equals(other.local))
            return false;
        if (protocol != other.protocol)
            return false;
        if (remote == null) {
            if (other.remote != null)
                return false;
        } else if (!remote.equals(other.remote))
            return false;
        return true;
    }
}
