package com.github.veithen.visualwas.connector.transport;

import java.io.IOException;

import org.apache.axiom.soap.SOAPEnvelope;

public interface Transport {
    void send(SOAPEnvelope request, TransportCallback callback) throws IOException;
}
