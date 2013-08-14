package com.github.veithen.visualwas.connector;

import com.github.veithen.visualwas.connector.security.Credentials;
import com.github.veithen.visualwas.connector.transport.TransportConfiguration;

public final class InvocationContext {
    private final ConnectorConfiguration connectorConfiguration;
    private final ClassLoader classLoader;
    private final Credentials credentials;
    
    InvocationContext(ConnectorConfiguration connectorConfiguration, Credentials credentials) {
        this.connectorConfiguration = connectorConfiguration;
        // Get the ClassLoader once when the context is created (i.e. at the beginning of the invocation)
        classLoader = connectorConfiguration.getClassLoaderProvider().getClassLoader();
        this.credentials = credentials;
    }
    
    public ClassMapper getClassMapper() {
        return connectorConfiguration.getClassMapper();
    }
    
    public ClassLoader getClassLoader() {
        return classLoader;
    }
    
    public TransportConfiguration getTransportConfiguration() {
        return connectorConfiguration.getTransportConfiguration();
    }

    public Credentials getCredentials() {
        return credentials;
    }
}
