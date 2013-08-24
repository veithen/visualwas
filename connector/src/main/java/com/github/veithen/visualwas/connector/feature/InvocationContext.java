package com.github.veithen.visualwas.connector.feature;

import com.github.veithen.visualwas.connector.transport.TransportConfiguration;

public interface InvocationContext {

    ClassLoader getClassLoader();

    TransportConfiguration getTransportConfiguration();

    <T> T getAttribute(Class<T> key);

    <T> void setAttribute(Class<T> key, T value);

}