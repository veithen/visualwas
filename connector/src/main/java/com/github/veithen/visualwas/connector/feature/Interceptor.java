package com.github.veithen.visualwas.connector.feature;

import org.apache.axiom.soap.SOAPEnvelope;

public interface Interceptor {
    void processRequest(SOAPEnvelope request, InvocationContext context);
}
