package com.github.veithen.visualwas.connector;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.github.veithen.visualwas.connector.feature.InvocationContext;
import com.github.veithen.visualwas.connector.feature.Serializer;

final class DefaultSerializer implements Serializer {
    static final DefaultSerializer INSTANCE = new DefaultSerializer();
    
    private DefaultSerializer() {}

    @Override
    public Object readObject(InputStream in, InvocationContext context) throws IOException, ClassNotFoundException {
        return new ConfigurableObjectInputStream(in, context.getClassLoader()).readObject();
    }

    @Override
    public void writeObject(Object object, OutputStream out, InvocationContext context) throws IOException {
        new ObjectOutputStream(out).writeObject(object);
    }
}
