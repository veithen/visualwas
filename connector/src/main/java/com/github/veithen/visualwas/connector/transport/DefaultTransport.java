package com.github.veithen.visualwas.connector.transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPEnvelope;

public class DefaultTransport implements Transport {
    private final URL endpointUrl;
    private final Proxy proxy;
    private final int connectTimeout;
    private final TrustManager trustManager;
    
    /**
     * Constructor.
     * 
     * @param endpointUrl
     *            the endpoint to connect to
     * @param proxy
     *            the proxy, or <code>null</code> to use the default proxy settings; use
     *            {@link Proxy#NO_PROXY} to force a direct connection
     * @param connectTimeout
     *            the connect timeout in milliseconds
     * @param trustManager
     *            the trust manager to use, or <code>null</code> to use the default trust manager;
     *            only used when connecting to an HTTPS endpoint
     */
    public DefaultTransport(URL endpointUrl, Proxy proxy, int connectTimeout, TrustManager trustManager) {
        this.endpointUrl = endpointUrl;
        this.proxy = proxy;
        this.connectTimeout = connectTimeout;
        this.trustManager = trustManager;
    }

    @Override
    public void send(SOAPEnvelope request, TransportCallback callback) throws IOException {
        try {
            HttpURLConnection conn = (HttpURLConnection)(proxy == null ? endpointUrl.openConnection() : endpointUrl.openConnection(proxy));
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
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
            conn.connect();
            OutputStream out = conn.getOutputStream();
            OMOutputFormat format = new OMOutputFormat();
            format.setCharSetEncoding("UTF-8");
            // TODO: this should actually throw IOException
            request.serialize(out);
            out.close();
            boolean isOK = conn.getResponseCode() == 200;
            InputStream in = isOK ? conn.getInputStream() : conn.getErrorStream();
            try {
                SOAPEnvelope response = OMXMLBuilderFactory.createSOAPModelBuilder(in, "UTF-8").getSOAPEnvelope(); // TODO: encoding!
                if (isOK) {
                    callback.onResponse(response);
                } else {
                    callback.onFault(response);
                }
            } finally {
                in.close();
            }
        } catch (XMLStreamException ex) {
            // TODO: attempt to unwrap the exception first
            throw new IOException(ex);
        }
    }
}
