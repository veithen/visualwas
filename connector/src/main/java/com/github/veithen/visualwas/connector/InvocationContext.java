package com.github.veithen.visualwas.connector;

import com.github.veithen.visualwas.connector.security.Credentials;
import com.github.veithen.visualwas.connector.transport.TransportConfiguration;

public final class InvocationContext {
    private final ConnectorConfiguration connectorConfiguration;
    private final ClassLoader classLoader;
    private final ClassMapper classMapper;
    private final Credentials credentials;
    
    InvocationContext(ConnectorConfiguration connectorConfiguration, ClassMapper classMapper, Credentials credentials) {
        this.connectorConfiguration = connectorConfiguration;
        // Get the ClassLoader once when the context is created (i.e. at the beginning of the invocation)
        classLoader = connectorConfiguration.getClassLoaderProvider().getClassLoader();
        this.classMapper = classMapper;
        this.credentials = credentials;
    }
    
    public ClassMapper getClassMapper() {
        return classMapper;
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
