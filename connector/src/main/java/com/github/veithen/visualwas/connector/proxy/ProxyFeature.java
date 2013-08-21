package com.github.veithen.visualwas.connector.proxy;

import com.github.veithen.visualwas.connector.feature.Configurator;
import com.github.veithen.visualwas.connector.feature.ConfiguratorAdapter;
import com.github.veithen.visualwas.connector.feature.Feature;

@ConfiguratorAdapter(ProxyConfigurator.class)
public final class ProxyFeature implements Feature {
    public static final ProxyFeature INSTANCE = new ProxyFeature();
    
    private ProxyFeature() {}

    @Override
    public void configureConnector(Configurator configurator) {
        configurator.registerConfiguratorAdapter(ProxyConfigurator.class, new ProxyConfiguratorImpl(configurator));
    }
}
