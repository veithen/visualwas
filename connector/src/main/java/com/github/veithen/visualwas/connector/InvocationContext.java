package com.github.veithen.visualwas.connector;

import com.github.veithen.visualwas.connector.feature.Serializer;
import com.github.veithen.visualwas.connector.transport.TransportConfiguration;

public final class InvocationContext {
    private final ConnectorConfiguration connectorConfiguration;
    private final ClassLoader classLoader;
    private final Serializer serializer;
    private final Attributes attributes;
    
    InvocationContext(ConnectorConfiguration connectorConfiguration, Serializer serializer, Attributes initialAttributes) {
        this.connectorConfiguration = connectorConfiguration;
        // Get the ClassLoader once when the context is created (i.e. at the beginning of the invocation)
        classLoader = connectorConfiguration.getClassLoaderProvider().getClassLoader();
        this.serializer = serializer;
        attributes = new Attributes(initialAttributes);
    }
    
    Serializer getSerializer() {
        return serializer;
    }
    
    public ClassLoader getClassLoader() {
        return classLoader;
    }
    
    public TransportConfiguration getTransportConfiguration() {
        return connectorConfiguration.getTransportConfiguration();
    }

    public <T> T getAttribute(Class<T> key) {
        return attributes.get(key);
    }

    public <T> void setAttribute(Class<T> key, T value) {
        attributes.set(key, value);
    }
}
