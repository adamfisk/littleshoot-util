package org.littleshoot.util;

import java.net.InetAddress;

/**
 * Interface for classes determining our public IP address.
 */
public interface PublicIp {
    
    InetAddress getPublicIpAddress();
}
