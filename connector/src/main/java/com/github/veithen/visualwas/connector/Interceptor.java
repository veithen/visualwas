package com.github.veithen.visualwas.connector;

import org.apache.axiom.soap.SOAPEnvelope;

public interface Interceptor {
    void processRequest(SOAPEnvelope request);
}
