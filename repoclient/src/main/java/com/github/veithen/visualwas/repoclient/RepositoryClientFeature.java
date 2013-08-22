package com.github.veithen.visualwas.repoclient;

import com.github.veithen.visualwas.connector.feature.Configurator;
import com.github.veithen.visualwas.connector.feature.Dependencies;
import com.github.veithen.visualwas.connector.feature.Feature;
import com.github.veithen.visualwas.connector.proxy.ProxyConfigurator;
import com.github.veithen.visualwas.connector.proxy.ProxyFeature;
import com.github.veithen.visualwas.connector.proxy.SingletonMBeanLocator;

@Dependencies(ProxyFeature.class)
public final class RepositoryClientFeature implements Feature {
    public static final RepositoryClientFeature INSTANCE = new RepositoryClientFeature();
    
    private RepositoryClientFeature() {}

    @Override
    public void configureConnector(Configurator configurator) {
        configurator.addAlternateClasses(
                DocumentContentSource.class,
                DocumentNotFoundException.class,
                RemoteSource.class);
        configurator.getAdapter(ProxyConfigurator.class).registerProxy(ConfigRepository.class, new SingletonMBeanLocator("ConfigRepository"));
    }
}
