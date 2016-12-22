/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2016 Andreas Veithen
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package com.github.veithen.visualwas.connector.feature;

import org.apache.axiom.soap.SOAPEnvelope;

import com.github.veithen.visualwas.connector.AdminService;
import com.github.veithen.visualwas.connector.Connector;
import com.github.veithen.visualwas.connector.Invocation;
import com.github.veithen.visualwas.connector.description.Interface;
import com.github.veithen.visualwas.framework.Adaptable;

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
     * returned by {@link Interface#getInterface()} will be registered as an adapter
     * and the corresponding adapter instance can be retrieved by invoking
     * {@link Adaptable#getAdapter(Class)} on the {@link Connector} instance. The
     * {@link AdminService} instance passed to {@link AdapterFactory#createAdapter(AdminService)}
     * will also implement this interface.
     * 
     * @param description
     *            the description of the operations to add
     */
    void addAdminServiceInterface(Interface description);
    
    void addInvocationInterceptor(Interceptor<Invocation,Object> interceptor);
    
    void addSOAPInterceptor(Interceptor<SOAPEnvelope,SOAPResponse> interceptor);
    
    <T> void registerConfiguratorAdapter(Class<T> iface, T adapter);
    
    <T> void registerAdminServiceAdapter(Class<T> iface, AdapterFactory<T> adapterFactory);
}
