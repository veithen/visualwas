package com.github.veithen.visualwas.connector;

import com.github.veithen.visualwas.connector.feature.Configurator;
import com.github.veithen.visualwas.connector.feature.Feature;

final class BaseFeature implements Feature {
    static final BaseFeature INSTANCE = new BaseFeature();
    
    private BaseFeature() {}

    @Override
    public void configureConnector(Configurator configurator) {
        configurator.addAlternateClasses(SOAPException.class);
    }
}
