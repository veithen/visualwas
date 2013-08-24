package com.github.veithen.visualwas.connector.impl;

import java.io.IOException;
import java.io.OutputStream;

import javax.activation.CommandMap;
import javax.activation.DataContentHandler;
import javax.activation.DataHandler;

/**
 * {@link DataHandler} implementation that outputs the enclosed object using Java serialization.
 * Note that the correct way to handle this would be to implement a {@link DataContentHandler} and
 * to map it to a content type using a {@link CommandMap}. However, it is much easier to extend
 * {@link DataHandler} and to just override {@link DataHandler#writeTo(OutputStream)}, especially
 * because we know that Axiom only uses that method (and never {@link DataHandler#getDataSource()}.
 */
public class ObjectDataHandler extends DataHandler {
    private final InvocationContextImpl context;
    
    public ObjectDataHandler(Object object, InvocationContextImpl context) {
        super(object, "application/x-java-object");
        this.context = context;
    }
    
    @Override
    public void writeTo(OutputStream out) throws IOException {
        context.getSerializer().writeObject(getContent(), out, context);
    }
}
