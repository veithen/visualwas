package com.github.veithen.visualwas.connector.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.veithen.visualwas.connector.feature.AdapterFactory;
import com.github.veithen.visualwas.connector.feature.Configurator;
import com.github.veithen.visualwas.connector.feature.Interceptor;
import com.github.veithen.visualwas.connector.feature.Serializer;

final class ConfiguratorImpl implements Configurator {
    private final Map<Class<?>,Object> adapters = new HashMap<Class<?>,Object>();
    private List<Interceptor> interceptors;
    private Serializer serializer = DefaultSerializer.INSTANCE;
    private AdaptableDelegate adaptableDelegate;

    ConfiguratorImpl(List<Interceptor> interceptors, AdaptableDelegate adaptableDelegate) {
        this.interceptors = interceptors;
        this.adaptableDelegate = adaptableDelegate;
    }

    @Override
    public void setSerializer(Serializer serializer) {
        if (this.serializer != DefaultSerializer.INSTANCE) {
            throw new IllegalStateException("Serializer already set");
        }
        this.serializer = serializer;
    }

    @Override
    public void addInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
    }

    @Override
    public <T> void registerConfiguratorAdapter(Class<T> iface, T adapter) {
        adapters.put(iface, adapter);
    }

    @Override
    public <T> T getAdapter(Class<T> clazz) {
        return clazz.cast(adapters.get(clazz));
    }

    @Override
    public <T> void registerAdminServiceAdapter(Class<T> iface, AdapterFactory<T> adapterFactory) {
        adaptableDelegate.registerAdapter(iface, adapterFactory);
    }

    Serializer getSerializer() {
        return serializer;
    }

    void release() {
        interceptors = null;
        adaptableDelegate = null;
    }
}
