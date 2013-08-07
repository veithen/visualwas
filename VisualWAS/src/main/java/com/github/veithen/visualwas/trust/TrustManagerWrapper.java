package com.github.veithen.visualwas.trust;

import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * {@link X509TrustManager} wrapper that allows to extract the certificate chain presented by the
 * server. This wrapper does two things:
 * <ol>
 * <li>If {@link X509TrustManager#checkServerTrusted(X509Certificate[], String)} fails, it wraps the
 * exception in a {@link NotTrustedException} that contains the certificate chain presented by the
 * server.
 * <li>Since it extends {@link X509ExtendedTrustManager} but delegates to the methods defined by
 * {@link X509TrustManager}, it will effectively disable host name checking. This is what we want
 * for WebSphere (and is compatible with the behavior of IBM's SOAP connector).
 * </ol>
 */
public final class TrustManagerWrapper extends X509ExtendedTrustManager {
    private final X509TrustManager parent;

    public TrustManagerWrapper(X509TrustManager parent) {
        this.parent = parent;
    }
    
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {
        throw new UnsupportedOperationException();
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            parent.checkServerTrusted(chain, authType);
        } catch (CertificateException ex) {
            throw new NotTrustedException(ex, chain);
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
        checkServerTrusted(chain, authType);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {
        checkServerTrusted(chain, authType);
    }

    public X509Certificate[] getAcceptedIssuers() {
        throw new UnsupportedOperationException();
    }
}
