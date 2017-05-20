package org.rouplex.nio.channels;

import org.rouplex.nio.channels.spi.SSLSelectorProvider;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;

/**
 * A subclass of a {@link SocketChannel} that provides same functionality as a SocketChannel but over a secured line
 * with the remote endpoints.
 * The various SSL configuration aspects, such as enabling particular secure protocols and ciphers, key and certificate
 * management, are handled via the {@link SSLContext} class, similar to the way it is done when {@link SSLSocket} class
 * is used.
 *
 * @author Andi Mullaraj (andimullaraj at gmail.com)
 */
public abstract class SSLSocketChannel extends SocketChannel {
    protected SSLSocketChannel(SSLSelectorProvider provider) {
        super(provider);
    }

    /**
     * Create an {@link SSLSocketChannel} using JVM's default security settings.
     *
     * @return The newly created {@link SSLSocketChannel}
     * @throws IOException
     *         If anything goes wrong during the creation
     */
    public static SSLSocketChannel open() throws IOException {
        return open(null, null, true, null, null);
    }

    /**
     * Create an {@link SSLSocketChannel} using JVM's default security settings and connect it to the
     * remote address before returning.
     *
     * @param socketAddress
     *         The socketAddress to connect right after the channels creation
     * @return The newly created {@link SSLSocketChannel}
     * @throws IOException
     *         If anything goes wrong during the creation
     */
    public static SSLSocketChannel open(SocketAddress socketAddress) throws IOException {
        return open(socketAddress, null, true, null, null);
    }

    /**
     * Create an {@link SSLSocketChannel} using security settings defined in {@link SSLContext}.
     *
     * @param sslContext
     *         The sslContext to be used or null to if JVM's default security settings are preferred
     * @return The newly created {@link SSLSocketChannel}
     * @throws IOException
     *         If anything goes wrong during the creation
     */
    public static SSLSocketChannel open(SSLContext sslContext) throws IOException {
        return open(null, sslContext, true, null, null);
    }

    /**
     * Create an {@link SSLSocketChannel} using security settings defined in {@link SSLContext} and connect it before
     * returning.
     *
     * @param socketAddress
     *         The socketAddress to connect right after the channels creation
     * @param sslContext
     *         An instance of {@link SSLContext} via which the caller defines the {@link KeyManager} and {@link
     *         TrustManager} providing the private keys and certificates for the encryption and
     *         authentication/authorization of the remote party.
     *         If this parameter is null, then the JRE's default sslContext instance, configured with JRE's defaults,
     *         will be used for the {@link SSLSocketChannel} instance being created.
     * @return The newly created {@link SSLSocketChannel}
     * @throws IOException
     *         If anything goes wrong during the creation
     */
    public static SSLSocketChannel open(SocketAddress socketAddress, SSLContext sslContext) throws IOException {
        return open(socketAddress, sslContext, true, null, null);
    }

    /**
     * Create an {@link SSLSocketChannel} using security settings defined in {@link SSLContext}, an existing (and
     * possibly connected) {@link SocketChannel} and connect it before returning.
     *
     * @param socketAddress
     *         The socketAddress to connect right after the channels creation
     * @param sslContext
     *         An instance of {@link SSLContext} via which the caller defines the {@link KeyManager} and {@link
     *         TrustManager} providing the private keys and certificates for the encryption and
     *         authentication/authorization of the remote party.
     *         If this parameter is null, then the JRE's default sslContext instance, configured with JRE's defaults,
     *         will be used for the {@link SSLSocketChannel} instance being created.
     * @param clientMode
     *         True if the channel will be used on the client side, false if on the server
     * @param tasksExecutorService
     *         The executor service to be used for the long standing {@link SSLEngine} tasks. If null, a default
     *         executor service, shared with other SSLSocketChannel instances, will be used.
     * @param innerChannel
     *         The inner channels to be used by the secure channels being created, if it exists. The innerChannel would
     *         exist in cases where the TCP connection has already been established (and possibly used) with the remote
     *         party.
     *         If null, a new channels will be created. If not null and not connected, the innerChannel will first be
     *         connected and then used by the secure one for the remainder of the session.
     * @return The newly created {@link SSLSocketChannel}
     * @throws IOException
     *         If anything goes wrong during the creation
     */
    public static SSLSocketChannel open(SocketAddress socketAddress, SSLContext sslContext,
            boolean clientMode, ExecutorService tasksExecutorService, SocketChannel innerChannel) throws IOException {

        SSLSocketChannel sslSocketChannel = SSLSelectorProvider.provider()
                .openSocketChannel(sslContext, clientMode, tasksExecutorService, innerChannel);

        if (socketAddress != null) {
            sslSocketChannel.connect(socketAddress);
        }

        return sslSocketChannel;
    }
}
