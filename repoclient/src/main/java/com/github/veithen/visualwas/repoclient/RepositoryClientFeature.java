package com.github.veithen.visualwas.repoclient;

import com.github.veithen.visualwas.connector.feature.Configurator;
import com.github.veithen.visualwas.connector.feature.Dependencies;
import com.github.veithen.visualwas.connector.feature.Feature;
import com.github.veithen.visualwas.connector.mapped.ClassMappingConfigurator;
import com.github.veithen.visualwas.connector.mapped.ClassMappingFeature;
import com.github.veithen.visualwas.connector.proxy.ProxyConfigurator;
import com.github.veithen.visualwas.connector.proxy.ProxyFeature;
import com.github.veithen.visualwas.connector.proxy.SingletonMBeanLocator;

@Dependencies({ClassMappingFeature.class, ProxyFeature.class})
public final class RepositoryClientFeature implements Feature {
    public static final RepositoryClientFeature INSTANCE = new RepositoryClientFeature();
    
    private RepositoryClientFeature() {}

    @Override
    public void configureConnector(Configurator configurator) {
        configurator.getAdapter(ClassMappingConfigurator.class).addMappedClasses(
                DocumentContentSource.class,
                DocumentNotFoundException.class,
                RemoteSource.class);
        configurator.getAdapter(ProxyConfigurator.class).registerProxy(ConfigRepository.class, new SingletonMBeanLocator("ConfigRepository"));
    }
}
