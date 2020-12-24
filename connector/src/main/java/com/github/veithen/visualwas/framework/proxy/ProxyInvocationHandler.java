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
package com.github.veithen.visualwas.framework.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

final class ProxyInvocationHandler implements InvocationHandler {
    private final Map<Method, InvocationHandlerDelegate> invocationHandlerDelegates;
    private final InvocationTarget invocationTarget;

    public ProxyInvocationHandler(
            Map<Method, InvocationHandlerDelegate> invocationHandlerDelegates,
            InvocationTarget invocationTarget) {
        this.invocationHandlerDelegates = invocationHandlerDelegates;
        this.invocationTarget = invocationTarget;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            String methodName = method.getName();
            if (methodName.equals("toString")) {
                return "<Proxy>@" + Integer.toHexString(System.identityHashCode(proxy));
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
        return invocationHandlerDelegates.get(method).invoke(invocationTarget, args);
    }
}
