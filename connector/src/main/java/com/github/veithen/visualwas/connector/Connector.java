package com.github.veithen.visualwas.connector;

import com.github.veithen.visualwas.connector.feature.CloseListener;
import com.github.veithen.visualwas.connector.feature.Feature;

public interface Connector extends AdminService, Adaptable {
    /**
     * Close this connector. Note that the underlying protocol (SOAP over HTTP) is connection-less,
     * i.e. there is no persistent TCP connection to the server that remains established over the
     * entire lifecycle of the connector. The purpose of this method is to allow {@link Feature
     * features} to perform cleanup and to release resources linked to the connector instance (such
     * as stopping threads).
     * 
     * @see CloseListener
     */
    void close();
}
