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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ObjectName;

import com.github.veithen.visualwas.connector.AdminService;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

final class ProxyInvocationHandler implements InvocationHandler {
    private final AdminService adminService;
    private final MBeanLocator locator;
    private ListenableFuture<ObjectName> mbeanFuture;
    
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
        try {
            return doInvoke(method.getName(), params, signature, false).get();
        } catch (ExecutionException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof MBeanException) {
                // MBeanException is a wrapper around exceptions thrown by MBeans. Unwrap the exception.
                cause = cause.getCause();
            }
            throw cause;
        }
    }

    private ListenableFuture<Object> doInvoke(final String operationName, final Object[] params, final String[] signature, final boolean isRetry) {
        final ListenableFuture<ObjectName> mbeanFuture;
        synchronized (this) {
            if (this.mbeanFuture == null) {
                this.mbeanFuture = locator.locateMBean(adminService);
            }
            mbeanFuture = this.mbeanFuture;
        }
        final SettableFuture<Object> futureResult = SettableFuture.create();
        Futures.addCallback(mbeanFuture, new FutureCallback<ObjectName>() {
            @Override
            public void onSuccess(ObjectName mbean) {
                Futures.addCallback(
                        adminService.invokeAsync(mbean, operationName, params, signature),
                        new FutureCallback<Object>() {
                            @Override
                            public void onSuccess(Object result) {
                                futureResult.set(result);
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                if (!isRetry && t instanceof InstanceNotFoundException) {
                                    synchronized (ProxyInvocationHandler.this) {
                                        if (ProxyInvocationHandler.this.mbeanFuture == mbeanFuture) {
                                            ProxyInvocationHandler.this.mbeanFuture = null;
                                        }
                                    }
                                    futureResult.setFuture(doInvoke(operationName, params, signature, true));
                                } else {
                                    futureResult.setException(t);
                                }
                            }
                        });
            }

            @Override
            public void onFailure(Throwable t) {
                futureResult.setException(t);
            }
        });
        return futureResult;
    }
}
