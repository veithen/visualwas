package com.github.veithen.visualwas.jmx.soap;

import javax.xml.namespace.QName;

import org.apache.axiom.soap.SOAPEnvelope;

import com.github.veithen.visualwas.connector.Interceptor;
import com.github.veithen.visualwas.connector.InvocationContext;

final class ConnectionIdInterceptor implements Interceptor {
    private static final QName HEADER_NAME = new QName("http://github.com/veithen/visualwas", "ConnectionId", "v");
    
    private final String connectionId;

    ConnectionIdInterceptor(String connectionId) {
        this.connectionId = connectionId;
    }

    @Override
    public void processRequest(SOAPEnvelope request, InvocationContext context) {
        request.getOrCreateHeader().addHeaderBlock(HEADER_NAME).setText(connectionId);
    }
}
