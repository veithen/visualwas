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
package com.github.veithen.visualwas.framework.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ProxyFactory {
    private ProxyFactory() {}
    
    public static <T> T createProxy(ClassLoader classLoader, Interface<T> iface, InvocationTarget target) {
        return iface.getInterface().cast(Proxy.newProxyInstance(
                classLoader,
                new Class<?>[] { iface.getInterface() },
                new ProxyInvocationHandler(
                        ((InterfaceImpl<?>)iface).getInvocationHandlerDelegates(),
                        target)));
    }
    
    public static Object createProxy(ClassLoader classLoader, Interface<?>[] ifaces, InvocationTarget target) {
        Set<Class<?>> javaInterfaces = new HashSet<>();
        Map<Method,InvocationHandlerDelegate> invocationHandlerDelegates = new HashMap<>();
        for (Interface<?> iface : ifaces) {
            javaInterfaces.add(iface.getInterface());
            invocationHandlerDelegates.putAll(((InterfaceImpl<?>)iface).getInvocationHandlerDelegates());
        }
        return Proxy.newProxyInstance(
                classLoader,
                javaInterfaces.toArray(new Class<?>[javaInterfaces.size()]),
                new ProxyInvocationHandler(
                        invocationHandlerDelegates,
                        target));
    }
}
