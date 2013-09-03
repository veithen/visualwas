package com.github.veithen.visualwas.connector.feature;

import com.github.veithen.visualwas.connector.Adaptable;
import com.github.veithen.visualwas.connector.Connector;
import com.github.veithen.visualwas.connector.description.AdminServiceDescription;

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
    
    /**
     * Add support for additional admin service operations. The corresponding Java interface (as
     * returned by {@link AdminServiceDescription#getInterface()} will be registered as an adapter
     * and the corresponding adapter instance can be retrieved by invoking
     * {@link Adaptable#getAdapter(Class)} on the {@link Connector} instance.
     * 
     * @param description
     *            the description of the operations to add
     */
    // TODO: explain how to invoke the methods defined by the interface
    void addAdminServiceDescription(AdminServiceDescription description);
    
    void addInterceptor(Interceptor interceptor);
    
    <T> void registerConfiguratorAdapter(Class<T> iface, T adapter);
    
    <T> void registerAdminServiceAdapter(Class<T> iface, AdapterFactory<T> adapterFactory);
}
