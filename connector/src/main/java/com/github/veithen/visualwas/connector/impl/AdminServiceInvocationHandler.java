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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import com.github.veithen.visualwas.connector.Handler;
import com.github.veithen.visualwas.connector.Invocation;

public class AdminServiceInvocationHandler implements InvocationHandler {
    private final Map<Method,InvocationHandlerDelegate> invocationHandlerDelegates;
    private final InvocationContextProvider invocationContextProvider;
    private final Handler<Invocation,Object> handler;

    public AdminServiceInvocationHandler(Map<Method,InvocationHandlerDelegate> invocationHandlerDelegates,
            InvocationContextProvider invocationContextProvider,
            Handler<Invocation,Object> handler) {
        this.invocationHandlerDelegates = invocationHandlerDelegates;
        this.invocationContextProvider = invocationContextProvider;
        this.handler = handler;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            String methodName = method.getName();
            if (methodName.equals("toString")) {
                return "<AdminService proxy>@" + Integer.toHexString(System.identityHashCode(proxy));
            } else if (methodName.equals("equals")) {
                return proxy == args[0];
            } else if (methodName.equals("hashCode")) {
                return System.identityHashCode(proxy);
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            return internalInvoke(method, args);
        }
    }
    
    private Object internalInvoke(Method method, Object[] args) throws Throwable {
        return invocationHandlerDelegates.get(method).invoke(args, handler, invocationContextProvider.get());
    }
}
