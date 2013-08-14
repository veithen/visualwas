package com.github.veithen.visualwas.jmx.soap;

import com.github.veithen.visualwas.connector.feature.ConnectorConfigurator;
import com.github.veithen.visualwas.connector.feature.Feature;

final class ConnectionIdFeature implements Feature {
    private final String connectionId;
    
    ConnectionIdFeature(String connectionId) {
        this.connectionId = connectionId;
    }

    @Override
    public void configureConnector(ConnectorConfigurator configurator) {
        configurator.addInterceptor(new ConnectionIdInterceptor(connectionId));
    }
}
