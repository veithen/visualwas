package com.github.veithen.visualwas.connector;

import com.github.veithen.visualwas.connector.feature.ConnectorConfigurator;
import com.github.veithen.visualwas.connector.feature.Feature;

final class BaseFeature implements Feature {
    static final BaseFeature INSTANCE = new BaseFeature();
    
    private BaseFeature() {}

    @Override
    public void configureConnector(ConnectorConfigurator configurator) {
        configurator.addAlternateClasses(SOAPException.class);
    }
}
