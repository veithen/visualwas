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
package com.github.veithen.visualwas.connector.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axiom.soap.SOAPEnvelope;

import com.github.veithen.visualwas.connector.AdminService;
import com.github.veithen.visualwas.connector.Invocation;
import com.github.veithen.visualwas.connector.description.Interface;
import com.github.veithen.visualwas.connector.feature.AdapterFactory;
import com.github.veithen.visualwas.connector.feature.Configurator;
import com.github.veithen.visualwas.connector.feature.Interceptor;
import com.github.veithen.visualwas.connector.feature.SOAPResponse;
import com.github.veithen.visualwas.connector.feature.Serializer;

final class ConfiguratorImpl implements Configurator {
    private final Map<Class<?>,Object> adapters = new HashMap<Class<?>,Object>();
    private List<Interface> adminServiceInterfaces;
    private InterceptorChainBuilder<Invocation,Object> invocationInterceptors;
    private InterceptorChainBuilder<SOAPEnvelope,SOAPResponse> soapInterceptors;
    private Serializer serializer = DefaultSerializer.INSTANCE;
    private AdaptableDelegate adaptableDelegate;

    ConfiguratorImpl(List<Interface> adminServiceInterfaces, InterceptorChainBuilder<Invocation,Object> invocationInterceptors, InterceptorChainBuilder<SOAPEnvelope,SOAPResponse> soapInterceptors, AdaptableDelegate adaptableDelegate) {
        this.adminServiceInterfaces = adminServiceInterfaces;
        this.invocationInterceptors = invocationInterceptors;
        this.soapInterceptors = soapInterceptors;
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
    public void addAdminServiceInterface(Interface iface) {
        adminServiceInterfaces.add(iface);
        registerAdminServerAdapterForExtension(iface.getInterface());
    }
    
    private <T> void registerAdminServerAdapterForExtension(final Class<T> iface) {
        adaptableDelegate.registerAdapter(iface, new AdapterFactory<T>() {
            @Override
            public T createAdapter(AdminService adminService) {
                return iface.cast(adminService);
            }
        });
    }

    @Override
    public void addInvocationInterceptor(Interceptor<Invocation,Object> interceptor) {
        invocationInterceptors.add(interceptor);
    }
    
    @Override
    public void addSOAPInterceptor(Interceptor<SOAPEnvelope,SOAPResponse> interceptor) {
        soapInterceptors.add(interceptor);
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
        adminServiceInterfaces = null;
        invocationInterceptors = null;
        soapInterceptors = null;
        adaptableDelegate = null;
    }
}
