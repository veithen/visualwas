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

import com.github.veithen.visualwas.connector.AdminService;
import com.github.veithen.visualwas.connector.Invocation;
import com.github.veithen.visualwas.connector.factory.Attributes;
import com.github.veithen.visualwas.connector.factory.ConnectorConfiguration;
import com.github.veithen.visualwas.connector.feature.Handler;
import com.github.veithen.visualwas.connector.feature.InvocationContext;
import com.github.veithen.visualwas.connector.feature.Serializer;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

final class InvocationContextImpl implements InvocationContext {
    private final AdminServiceFactory adminServiceFactory;
    private final ClassLoader classLoader;
    private final ListeningExecutorService executor;
    private final Serializer serializer;
    private final Attributes attributes;
    
    InvocationContextImpl(ConnectorConfiguration connectorConfiguration, AdminServiceFactory adminServiceFactory,
            Serializer serializer, Attributes initialAttributes) {
        this.adminServiceFactory = adminServiceFactory;
        // Get the ClassLoader once when the context is created (i.e. at the beginning of the invocation)
        classLoader = connectorConfiguration.getClassLoaderProvider().getClassLoader();
        executor = MoreExecutors.newDirectExecutorService();
        this.serializer = serializer;
        attributes = new Attributes(initialAttributes);
    }
    
    Serializer getSerializer() {
        return serializer;
    }
    
    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }
    
    @Override
    public ListeningExecutorService getExecutor() {
        return executor;
    }

    @Override
    public <T> T getAttribute(Class<T> key) {
        return attributes.get(key);
    }

    @Override
    public <T> void setAttribute(Class<T> key, T value) {
        attributes.set(key, value);
    }

    @Override
    public AdminService getAdminService(Handler<Invocation, Object> handler) {
        return adminServiceFactory.create(
                new InvocationContextProvider() {
                    @Override
                    public InvocationContextImpl get() {
                        return InvocationContextImpl.this;
                    }
                },
                handler);
    }
}
