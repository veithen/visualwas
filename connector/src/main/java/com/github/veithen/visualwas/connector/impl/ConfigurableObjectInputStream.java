package com.github.veithen.visualwas.connector.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

final class ConfigurableObjectInputStream extends ObjectInputStream {
    private final ClassLoader classLoader;
    
    ConfigurableObjectInputStream(InputStream in, ClassLoader classLoader) throws IOException {
        super(in);
        this.classLoader = classLoader;
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        return Class.forName(desc.getName(), false, classLoader);
    }
}