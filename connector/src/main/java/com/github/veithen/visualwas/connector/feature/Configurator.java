package com.github.veithen.visualwas.connector.feature;

import com.github.veithen.visualwas.connector.Adaptable;

/**
 * Defines the API used by {@link Feature} objects to configure a connector instance. Instances of
 * this instance are only valid during the invocation of
 * {@link Feature#configureConnector(ConnectorConfigurator)}.
 */
public interface Configurator extends Adaptable {
    /**
     * Configure an alternate {@link Serializer}. Note that only a single {@link Feature} can
     * override the default implementation.
     * 
     * @param serializer
     *            the serializer to use
     * @throws IllegalStateException
     *             if the serializer has already been set by another feature
     */
    void setSerializer(Serializer serializer);
    
    void addInterceptor(Interceptor interceptor);
    
    <T> void registerConfiguratorAdapter(Class<T> iface, T adapter);
    
    <T> void registerAdminServiceAdapter(Class<T> iface, AdapterFactory<T> adapterFactory);
}
