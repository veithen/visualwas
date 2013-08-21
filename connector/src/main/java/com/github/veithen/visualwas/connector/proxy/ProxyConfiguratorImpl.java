package com.github.veithen.visualwas.connector.proxy;

import com.github.veithen.visualwas.connector.feature.Configurator;

final class ProxyConfiguratorImpl implements ProxyConfigurator {
    private final Configurator configurator;

    ProxyConfiguratorImpl(Configurator configurator) {
        this.configurator = configurator;
    }

    @Override
    public <T> void registerProxy(Class<T> iface, MBeanLocator locator) {
        configurator.registerAdminServiceAdapter(iface, new ProxyAdapterFactory<T>(iface, locator));
    }
}
