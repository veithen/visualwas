package com.github.veithen.visualwas.connector.transport;

import org.apache.axiom.soap.SOAPEnvelope;

public interface TransportCallback {
    void onResponse(SOAPEnvelope envelope);
    void onFault(SOAPEnvelope envelope);
}
