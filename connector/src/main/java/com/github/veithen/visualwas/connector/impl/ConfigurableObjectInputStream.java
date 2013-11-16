package com.github.veithen.visualwas.connector.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * {@link ObjectInputStream} subclass that resolves class names using a specified class loader.
 */
final class ConfigurableObjectInputStream extends ObjectInputStream {
    private final ClassLoader classLoader;
    
    /**
     * Constructor.
     * 
     * @param in
     *            the input stream to read from
     * @param classLoader
     *            the class loader to load classes from
     * @throws IOException
     *             if an I/O error occurs while reading the stream header
     */
    ConfigurableObjectInputStream(InputStream in, ClassLoader classLoader) throws IOException {
        super(in);
        this.classLoader = classLoader;
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        // Note: we can't use ClassLoader#loadClass here because the name may denote an array class
        return Class.forName(desc.getName(), false, classLoader);
    }
}
