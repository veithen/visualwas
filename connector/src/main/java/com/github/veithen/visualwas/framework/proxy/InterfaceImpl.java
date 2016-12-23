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
package com.github.veithen.visualwas.framework.proxy;

import java.lang.reflect.Method;
import java.util.Map;

final class InterfaceImpl<T> implements Interface<T> {
    private final Class<T> iface;
    private final Map<String,Operation> operations;
    private final Map<Method,InvocationHandlerDelegate> invocationHandlerDelegates;

    InterfaceImpl(Class<T> iface, Map<String,Operation> operations, Map<Method,InvocationHandlerDelegate> invocationHandlerDelegates) {
        this.iface = iface;
        this.operations = operations;
        this.invocationHandlerDelegates = invocationHandlerDelegates;
    }

    @Override
    public Class<T> getInterface() {
        return iface;
    }

    @Override
    public Operation getOperation(String name) {
        return operations.get(name);
    }

    Map<Method,InvocationHandlerDelegate> getInvocationHandlerDelegates() {
        return invocationHandlerDelegates;
    }
}
