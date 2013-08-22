package com.github.veithen.visualwas.connector.altclasses;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.github.veithen.visualwas.connector.InvocationContext;
import com.github.veithen.visualwas.connector.feature.Serializer;

final class SerializerImpl implements Serializer {
    private final ClassMapper classMapper;
    
    SerializerImpl(ClassMapper classMapper) {
        this.classMapper = classMapper;
    }

    @Override
    public Object readObject(InputStream in, InvocationContext context) throws IOException, ClassNotFoundException {
        return new ConfigurableObjectInputStream(in, classMapper, context).readObject();
    }

    @Override
    public void writeObject(Object object, OutputStream out, InvocationContext context) throws IOException {
        new ConfigurableObjectOutputStream(out, classMapper, context).writeObject(object);
    }
}
