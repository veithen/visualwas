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
