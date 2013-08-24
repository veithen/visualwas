package com.github.veithen.visualwas.connector.impl;

import com.github.veithen.visualwas.connector.Attributes;
import com.github.veithen.visualwas.connector.ConnectorConfiguration;
import com.github.veithen.visualwas.connector.feature.InvocationContext;
import com.github.veithen.visualwas.connector.feature.Serializer;
import com.github.veithen.visualwas.connector.transport.TransportConfiguration;

final class InvocationContextImpl implements InvocationContext {
    private final ConnectorConfiguration connectorConfiguration;
    private final ClassLoader classLoader;
    private final Serializer serializer;
    private final Attributes attributes;
    
    InvocationContextImpl(ConnectorConfiguration connectorConfiguration, Serializer serializer, Attributes initialAttributes) {
        this.connectorConfiguration = connectorConfiguration;
        // Get the ClassLoader once when the context is created (i.e. at the beginning of the invocation)
        classLoader = connectorConfiguration.getClassLoaderProvider().getClassLoader();
        this.serializer = serializer;
        attributes = new Attributes(initialAttributes);
    }
    
    Serializer getSerializer() {
        return serializer;
    }
    
    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }
    
    @Override
    public TransportConfiguration getTransportConfiguration() {
        return connectorConfiguration.getTransportConfiguration();
    }

    @Override
    public <T> T getAttribute(Class<T> key) {
        return attributes.get(key);
    }

    @Override
    public <T> void setAttribute(Class<T> key, T value) {
        attributes.set(key, value);
    }
}
