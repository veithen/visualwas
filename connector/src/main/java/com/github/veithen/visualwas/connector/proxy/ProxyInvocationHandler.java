/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2014 Andreas Veithen
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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ObjectName;

import com.github.veithen.visualwas.connector.AdminService;

final class ProxyInvocationHandler implements InvocationHandler {
    private final AdminService adminService;
    private final MBeanLocator locator;
    private ObjectName mbean;
    
    ProxyInvocationHandler(AdminService adminService, MBeanLocator locator) {
        this.adminService = adminService;
        this.locator = locator;
    }

    public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {
        // Normalize the params argument
        if (params != null && params.length == 0) {
            params = null;
        }
        String[] signature;
        if (params == null) {
            signature = null;
        } else {
            Class<?>[] paramTypes = method.getParameterTypes();
            signature = new String[paramTypes.length];
            for (int i=0; i<paramTypes.length; i++) {
                signature[i] = paramTypes[i].getName();
            }
        }
        int attempts = 0;
        while (true) {
            synchronized (this) {
                if (mbean == null) {
                    mbean = locator.locateMBean(adminService);
                }
            }
            attempts++;
            try {
                return adminService.invoke(mbean, method.getName(), params, signature);
            } catch (InstanceNotFoundException ex) {
                if (attempts == 2) {
                    throw ex;
                } else {
                    synchronized (this) {
                        mbean = null;
                    }
                }
            } catch (MBeanException ex) {
                // MBean is a wrapper around exceptions thrown by MBeans. Unwrap the exception.
                throw ex.getCause();
            }
        }
    }
}
