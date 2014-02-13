/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2014 Andreas Veithen
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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.axiom.soap.SOAPEnvelope;

import com.github.veithen.visualwas.connector.AdminService;
import com.github.veithen.visualwas.connector.Handler;
import com.github.veithen.visualwas.connector.description.AdminServiceDescription;
import com.github.veithen.visualwas.connector.feature.AdapterFactory;
import com.github.veithen.visualwas.connector.feature.AdminServiceInterceptor;
import com.github.veithen.visualwas.connector.feature.Configurator;
import com.github.veithen.visualwas.connector.feature.Interceptor;
import com.github.veithen.visualwas.connector.feature.Serializer;

final class ConfiguratorImpl implements Configurator {
    private final Map<Class<?>,Object> adapters = new HashMap<Class<?>,Object>();
    private Set<Class<?>> adminServiceInterfaces;
    private Map<Method,OperationHandler> operationHandlers;
    private List<AdminServiceInterceptor> adminServiceInterceptors;
    private Handler<SOAPEnvelope,SOAPEnvelope,SOAPEnvelope> soapHandler;
    private Serializer serializer = DefaultSerializer.INSTANCE;
    private AdaptableDelegate adaptableDelegate;

    ConfiguratorImpl(Set<Class<?>> adminServiceInterfaces, Map<Method,OperationHandler> operationHandlers, List<AdminServiceInterceptor> adminServiceInterceptors, Handler<SOAPEnvelope,SOAPEnvelope,SOAPEnvelope> soapHandler, AdaptableDelegate adaptableDelegate) {
        this.adminServiceInterfaces = adminServiceInterfaces;
        this.operationHandlers = operationHandlers;
        this.adminServiceInterceptors = adminServiceInterceptors;
        this.soapHandler = soapHandler;
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
    public void addAdminServiceDescription(AdminServiceDescription description) {
        adminServiceInterfaces.add(description.getInterface());
        operationHandlers.putAll(((AdminServiceDescriptionImpl)description).getOperationHandlers());
        registerAdminServerAdapterForExtension(description.getInterface());
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
    public void addInterceptor(AdminServiceInterceptor interceptor) {
        adminServiceInterceptors.add(interceptor);
    }
    
    @Override
    public void addInterceptor(Interceptor<SOAPEnvelope,SOAPEnvelope,SOAPEnvelope> interceptor) {
        soapHandler = new InterceptorHandler<>(interceptor, soapHandler);
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

    Handler<SOAPEnvelope,SOAPEnvelope,SOAPEnvelope> getSOAPHandler() {
        return soapHandler;
    }

    void release() {
        adminServiceInterfaces = null;
        operationHandlers = null;
        adminServiceInterceptors = null;
        soapHandler = null;
        adaptableDelegate = null;
    }
}
