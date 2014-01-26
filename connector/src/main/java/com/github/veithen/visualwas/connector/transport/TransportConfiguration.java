/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2014 Andreas Veithen
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
package com.github.veithen.visualwas.connector.transport;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

public final class TransportConfiguration {
    public final static class Builder {
        private Proxy proxy;
        private int connectTimeout;
        private TrustManager trustManager;
        
        /**
         * Set the proxy.
         * 
         * @param proxy
         *            the proxy, or <code>null</code> to use the default proxy settings; use
         *            {@link Proxy#NO_PROXY} to force a direct connection
         * @return this builder
         */
        public Builder setProxy(Proxy proxy) {
            this.proxy = proxy;
            return this;
        }

        /**
         * Set the connect timeout.
         * 
         * @param connectTimeout
         *            the connect timeout in milliseconds
         * @return this builder
         */
        public Builder setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }
        
        /**
         * Set the trust manager.
         * 
         * @param trustManager
         *            the trust manager to use, or <code>null</code> to use the default trust
         *            manager; only used when connecting to an HTTPS endpoint
         * @return this builder
         */
        public Builder setTrustManager(TrustManager trustManager) {
            this.trustManager = trustManager;
            return this;
        }
        
        /**
         * Disable certificate validation. This method configures a special trust manager that
         * accepts any certificate. This turns off the validation of the trust chain as well as host
         * name validation.
         * <p>
         * <b>Note:</b> This method should only be used for testing purposes!
         * 
         * @return this builder
         */
        public Builder disableCertificateValidation() {
            return setTrustManager(DummyTrustManager.INSTANCE);
        }
        
        public TransportConfiguration build() {
            return new TransportConfiguration(proxy, connectTimeout, trustManager);
        }
    }
    
    public static final TransportConfiguration DEFAULT = new TransportConfiguration(null, 0, null);
    
    private final Proxy proxy;
    private final int connectTimeout;
    private final TrustManager trustManager;
    
    TransportConfiguration(Proxy proxy, int connectTimeout, TrustManager trustManager) {
        this.proxy = proxy;
        this.connectTimeout = connectTimeout;
        this.trustManager = trustManager;
    }

    public static Builder custom() {
        return new Builder();
    }
    
    public Proxy getProxy() {
        return proxy;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public TrustManager getTrustManager() {
        return trustManager;
    }
    
    public HttpURLConnection createURLConnection(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection)(proxy == null ? url.openConnection() : url.openConnection(proxy));
        conn.setConnectTimeout(connectTimeout);
        if (trustManager != null && conn instanceof HttpsURLConnection) {
            try {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, new TrustManager[] { trustManager }, new SecureRandom());
                ((HttpsURLConnection)conn).setSSLSocketFactory(sslContext.getSocketFactory());
            } catch (GeneralSecurityException ex) {
                throw new IOException("Failed to initialize SSL context", ex);
            }
        }
        return conn;
    }
}
