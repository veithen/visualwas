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
package com.github.veithen.visualwas.connector.proxy;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.github.veithen.visualwas.connector.AdminService;

final class ProxyHelper {
    private ProxyHelper() {}

    static <T> T createProxy(AdminService adminService, Class<T> iface, MBeanLocator locator) {
        for (Method method : iface.getMethods()) {
            boolean throwsIOException = false;
            for (Class<?> exceptionType : method.getExceptionTypes()) {
                if (exceptionType.isAssignableFrom(IOException.class)) {
                    throwsIOException = true;
                    break;
                }
            }
            if (!throwsIOException) {
                throw new IllegalArgumentException("All proxy methods must throw IOException");
            }
        }
        // TODO: correct class loader?
        return iface.cast(Proxy.newProxyInstance(ProxyAdapterFactory.class.getClassLoader(), new Class<?>[] { iface }, new ProxyInvocationHandler(adminService, locator)));
    }
}
