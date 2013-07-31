package com.github.veithen.visualwas.connector.transport;

import org.apache.axiom.soap.SOAPEnvelope;

public interface Transport {
    void send(SOAPEnvelope request, TransportCallback callback) throws TransportException;
}
