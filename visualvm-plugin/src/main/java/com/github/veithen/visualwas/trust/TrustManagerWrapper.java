/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2020 Andreas Veithen
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
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
 *
 * <ol>
 *   <li>If {@link X509TrustManager#checkServerTrusted(X509Certificate[], String)} fails, it wraps
 *       the exception in a {@link NotTrustedException} that contains the certificate chain
 *       presented by the server.
 *   <li>Since it extends {@link X509ExtendedTrustManager} but delegates to the methods defined by
 *       {@link X509TrustManager}, it will effectively disable host name checking. This is what we
 *       want for WebSphere (and is compatible with the behavior of IBM's SOAP connector).
 * </ol>
 */
public final class TrustManagerWrapper extends X509ExtendedTrustManager {
    private final X509TrustManager parent;

    public TrustManagerWrapper(X509TrustManager parent) {
        this.parent = parent;
    }

    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket)
            throws CertificateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
            throws CertificateException {
        throw new UnsupportedOperationException();
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        try {
            parent.checkServerTrusted(chain, authType);
        } catch (CertificateException ex) {
            throw new NotTrustedException(ex, chain);
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket)
            throws CertificateException {
        checkServerTrusted(chain, authType);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
            throws CertificateException {
        checkServerTrusted(chain, authType);
    }

    public X509Certificate[] getAcceptedIssuers() {
        throw new UnsupportedOperationException();
    }
}
