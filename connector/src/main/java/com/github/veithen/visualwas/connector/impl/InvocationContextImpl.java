/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2019 Andreas Veithen
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

import java.util.concurrent.Executor;

import com.github.veithen.visualwas.connector.AdminService;
import com.github.veithen.visualwas.connector.Attributes;
import com.github.veithen.visualwas.connector.feature.Handler;
import com.github.veithen.visualwas.connector.feature.InvocationContext;
import com.github.veithen.visualwas.connector.feature.Serializer;
import com.github.veithen.visualwas.framework.proxy.Invocation;

final class InvocationContextImpl implements InvocationContext {
    private final AdminServiceFactory adminServiceFactory;
    private final ClassLoader classLoader;
    private final Executor executor;
    private final Serializer serializer;
    private final Attributes attributes;
    
    InvocationContextImpl(AdminServiceFactory adminServiceFactory, ClassLoader classLoader,
            Executor executor, Serializer serializer, Attributes attributes) {
        this.classLoader = classLoader;
        this.adminServiceFactory = adminServiceFactory;
        this.executor = executor;
        this.serializer = serializer;
        this.attributes = attributes;
    }
    
    Serializer getSerializer() {
        return serializer;
    }
    
    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }
    
    @Override
    public Executor getExecutor() {
        return executor;
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    @Override
    public <T> T getAttribute(Class<T> key) {
        return attributes.get(key);
    }

    @Override
    public AdminService getAdminService(Handler<Invocation, Object> handler) {
        return adminServiceFactory.create(
                () -> InvocationContextImpl.this,
                handler,
                false);
    }

    @Override
    public InvocationContext withAttributes(Attributes attributes) {
        return new InvocationContextImpl(adminServiceFactory, classLoader, executor, serializer, attributes);
    }
}
