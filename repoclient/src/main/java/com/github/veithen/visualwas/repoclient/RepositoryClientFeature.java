package com.github.veithen.visualwas.repoclient;

import com.github.veithen.visualwas.connector.feature.ConnectorConfigurator;
import com.github.veithen.visualwas.connector.feature.Feature;

public final class RepositoryClientFeature implements Feature {
    public static final RepositoryClientFeature INSTANCE = new RepositoryClientFeature();
    
    private RepositoryClientFeature() {}

    @Override
    public void configureConnector(ConnectorConfigurator configurator) {
        configurator.addAlternateClasses(
                DocumentContentSource.class,
                DocumentNotFoundException.class,
                RemoteSource.class);
    }
}
