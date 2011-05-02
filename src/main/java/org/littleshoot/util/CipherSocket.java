package org.littleshoot.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.SocketChannel;

/**
 * Encrypting and decrypting and HMAC verifying socket.
 */
public class CipherSocket extends Socket {
    
    private final byte[] writeKey;
    private final byte[] readKey;
    private final Socket sock;

    public CipherSocket(final Socket sock, final byte[] writeKey, 
        final byte[] readKey) {
        this.sock = sock;
        this.writeKey = writeKey;
        this.readKey = readKey;
    }
    

    @Override
    public OutputStream getOutputStream() throws IOException {
        return new EncryptingOutputStream(this.writeKey, sock.getOutputStream());
    }
    

    @Override
    public InputStream getInputStream() throws IOException {
        return new DecryptingInputStream(this.readKey, sock.getInputStream());
    }

    @Override
    public void bind(SocketAddress bindpoint) throws IOException {
        sock.bind(bindpoint);
    }

    @Override
    public synchronized void close() throws IOException {
        sock.close();
    }

    @Override
    public void connect(SocketAddress endpoint, int timeout) 
        throws IOException {
        sock.connect(endpoint, timeout);
    }

    @Override
    public void connect(SocketAddress endpoint) throws IOException {
        sock.connect(endpoint);
    }

    @Override
    public SocketChannel getChannel() {
        return sock.getChannel();
    }

    @Override
    public InetAddress getInetAddress() {
        return sock.getInetAddress();
    }

    @Override
    public boolean getKeepAlive() throws SocketException {
        return sock.getKeepAlive();
    }

    @Override
    public InetAddress getLocalAddress() {
        return sock.getLocalAddress();
    }

    @Override
    public int getLocalPort() {
        return sock.getLocalPort();
    }

    @Override
    public SocketAddress getLocalSocketAddress() {
        return sock.getLocalSocketAddress();
    }

    @Override
    public boolean getOOBInline() throws SocketException {
        return sock.getOOBInline();
    }

    @Override
    public int getPort() {
        return sock.getPort();
    }

    @Override
    public synchronized int getReceiveBufferSize() throws SocketException {
        return sock.getReceiveBufferSize();
    }

    @Override
    public SocketAddress getRemoteSocketAddress() {
        return sock.getRemoteSocketAddress();
    }

    @Override
    public boolean getReuseAddress() throws SocketException {
        return sock.getReuseAddress();
    }

    @Override
    public synchronized int getSendBufferSize() throws SocketException {
        return sock.getSendBufferSize();
    }

    @Override
    public int getSoLinger() throws SocketException {
        return sock.getSoLinger();
    }

    @Override
    public synchronized int getSoTimeout() throws SocketException {
        return sock.getSoTimeout();
    }

    @Override
    public boolean getTcpNoDelay() throws SocketException {
        return sock.getTcpNoDelay();
    }

    @Override
    public int getTrafficClass() throws SocketException {
        return sock.getTrafficClass();
    }

    @Override
    public boolean isBound() {
        return sock.isBound();
    }

    @Override
    public boolean isClosed() {
        return sock.isClosed();
    }

    @Override
    public boolean isConnected() {
        return sock.isConnected();
    }

    @Override
    public boolean isInputShutdown() {
        return sock.isInputShutdown();
    }

    @Override
    public boolean isOutputShutdown() {
        return sock.isOutputShutdown();
    }

    @Override
    public void sendUrgentData(int data) throws IOException {
        sock.sendUrgentData(data);
    }

    @Override
    public void setKeepAlive(boolean on) throws SocketException {
        sock.setKeepAlive(on);
    }

    @Override
    public void setOOBInline(boolean on) throws SocketException {
        sock.setOOBInline(on);
    }

    @Override
    public void setPerformancePreferences(int connectionTime, int latency,
            int bandwidth) {
        sock.setPerformancePreferences(connectionTime, latency, bandwidth);
    }

    @Override
    public synchronized void setReceiveBufferSize(int size)
            throws SocketException {
        sock.setReceiveBufferSize(size);
    }

    @Override
    public void setReuseAddress(boolean on) throws SocketException {
        sock.setReuseAddress(on);
    }

    @Override
    public synchronized void setSendBufferSize(int size) throws SocketException {
        sock.setSendBufferSize(size);
    }

    @Override
    public void setSoLinger(boolean on, int linger) throws SocketException {
        sock.setSoLinger(on, linger);
    }

    @Override
    public synchronized void setSoTimeout(int timeout) throws SocketException {
        sock.setSoTimeout(timeout);
    }

    @Override
    public void setTcpNoDelay(boolean on) throws SocketException {
        sock.setTcpNoDelay(on);
    }

    @Override
    public void setTrafficClass(int tc) throws SocketException {
        sock.setTrafficClass(tc);
    }

    @Override
    public void shutdownInput() throws IOException {
        sock.shutdownInput();
    }

    @Override
    public void shutdownOutput() throws IOException {
        sock.shutdownOutput();
    }

    @Override
    public String toString() {
        return "CipherSocket [sock=" + sock + "]";
    }

}
