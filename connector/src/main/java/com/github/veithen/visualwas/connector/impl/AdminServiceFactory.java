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

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import com.github.veithen.visualwas.connector.AdminService;
import com.github.veithen.visualwas.connector.Invocation;
import com.github.veithen.visualwas.connector.feature.Handler;
import com.github.veithen.visualwas.framework.proxy.InvocationTarget;
import com.github.veithen.visualwas.framework.proxy.Operation;
import com.google.common.util.concurrent.ListenableFuture;

final class AdminServiceFactory {
    private final Class<?>[] ifaces;
    private final Map<Method,InvocationHandlerDelegate> invocationHandlerDelegates;

    AdminServiceFactory(Class<?>[] ifaces, Map<Method,InvocationHandlerDelegate> invocationHandlerDelegates) {
        this.ifaces = ifaces;
        this.invocationHandlerDelegates = invocationHandlerDelegates;
    }

    AdminService create(final InvocationContextProvider invocationContextProvider,
            final Handler<Invocation,Object> handler) {
        return (AdminService)Proxy.newProxyInstance(
                AdminServiceFactory.class.getClassLoader(),
                ifaces,
                new AdminServiceInvocationHandler(
                        invocationHandlerDelegates,
                        new InvocationTarget() {
                            @Override
                            public ListenableFuture<?> invoke(Operation operation, Object[] args) {
                                return handler.invoke(invocationContextProvider.get(), new Invocation(operation, args));
                            }
                        }));
        
    }
}
