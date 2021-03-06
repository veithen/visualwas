/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2020 Andreas Veithen
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

import java.util.function.Supplier;

import com.github.veithen.visualwas.connector.AdminService;
import com.github.veithen.visualwas.connector.feature.Handler;
import com.github.veithen.visualwas.framework.proxy.Interface;
import com.github.veithen.visualwas.framework.proxy.Invocation;
import com.github.veithen.visualwas.framework.proxy.InvocationStyle;
import com.github.veithen.visualwas.framework.proxy.ProxyFactory;

final class AdminServiceFactory {
    private final Interface<?>[] ifaces;

    AdminServiceFactory(Interface<?>[] ifaces) {
        this.ifaces = ifaces;
    }

    AdminService create(
            Supplier<InvocationContextImpl> invocationContextSupplier,
            Handler<Invocation, Object> handler,
            boolean allowSync) {
        return (AdminService)
                ProxyFactory.createProxy(
                        AdminServiceFactory.class.getClassLoader(),
                        ifaces,
                        invocation -> {
                            if (!allowSync
                                    && invocation.getInvocationStyle() == InvocationStyle.SYNC) {
                                throw new UnsupportedOperationException(
                                        "Synchronous invocations not allowed");
                            }
                            return handler.invoke(invocationContextSupplier.get(), invocation);
                        });
    }
}
