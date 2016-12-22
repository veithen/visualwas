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
import java.util.Map;

import com.github.veithen.visualwas.connector.description.Interface;
import com.github.veithen.visualwas.connector.description.Operation;

final class InterfaceImpl implements Interface {
    private final Class<?> iface;
    private final Map<String,Operation> operations;
    private final Map<Method,InvocationHandlerDelegate> invocationHandlerDelegates;

    InterfaceImpl(Class<?> iface, Map<String,Operation> operations, Map<Method,InvocationHandlerDelegate> invocationHandlerDelegates) {
        this.iface = iface;
        this.operations = operations;
        this.invocationHandlerDelegates = invocationHandlerDelegates;
    }

    @Override
    public Class<?> getInterface() {
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