package com.github.veithen.visualwas.connector.transport;

import java.net.MalformedURLException;
import java.net.URL;

public final class Endpoint {
    private final String host;
    private final int port;
    private final boolean securityEnabled;
    
    public Endpoint(String host, int port, boolean securityEnabled) {
        this.host = host;
        this.port = port;
        this.securityEnabled = securityEnabled;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
    
    public boolean isSecurityEnabled() {
        return securityEnabled;
    }

    public URL createURL() {
        try {
            return new URL(securityEnabled ? "https" : "http", host, port, "/");
        } catch (MalformedURLException ex) {
            throw new Error("Unexpected exception", ex);
        }
    }
}
