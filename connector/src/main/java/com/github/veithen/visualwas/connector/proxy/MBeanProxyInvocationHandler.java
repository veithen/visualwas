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
package com.github.veithen.visualwas.connector.proxy;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Function;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ObjectName;

import com.github.veithen.visualwas.connector.AdminService;
import com.github.veithen.visualwas.framework.proxy.Invocation;
import com.github.veithen.visualwas.framework.proxy.InvocationTarget;
import com.github.veithen.visualwas.framework.proxy.Operation;

final class MBeanProxyInvocationHandler implements InvocationTarget {
    private final AdminService adminService;
    private final MBeanLocator locator;
    private CompletableFuture<ObjectName> mbeanFuture;
    
    MBeanProxyInvocationHandler(AdminService adminService, MBeanLocator locator) {
        this.adminService = adminService;
        this.locator = locator;
    }

    @Override
    public CompletableFuture<?> invoke(Invocation invocation) {
        Operation operation = invocation.getOperation();
        // Normalize the params argument
        Object[] params = invocation.getParameters();
        if (params != null && params.length == 0) {
            params = null;
        }
        String[] signature;
        if (params == null) {
            signature = null;
        } else {
            Class<?>[] paramTypes = operation.getSignature();
            signature = new String[paramTypes.length];
            for (int i=0; i<paramTypes.length; i++) {
                signature[i] = paramTypes[i].getName();
            }
        }
        return doInvoke(operation.getName(), params, signature, false);
    }

    private CompletableFuture<Object> doInvoke(final String operationName, final Object[] params, final String[] signature, final boolean isRetry) {
        final CompletableFuture<ObjectName> mbeanFuture;
        synchronized (this) {
            if (this.mbeanFuture == null) {
                this.mbeanFuture = locator.locateMBean(adminService);
            }
            mbeanFuture = this.mbeanFuture;
        }
        return mbeanFuture
                .thenCompose(mbean -> adminService.invokeAsync(mbean, operationName, params, signature))
                // There is no thenCompose that handles exception. Instead, produce a CompletableFuture
                // with handle and then use thenCompose with the identity function.
                .handle((result, t) -> {
                    if (t == null) {
                        return CompletableFuture.completedFuture(result);
                    }
                    if (t instanceof CompletionException) {
                        t = t.getCause();
                    }
                    if (!isRetry && t instanceof InstanceNotFoundException) {
                        synchronized (MBeanProxyInvocationHandler.this) {
                            if (MBeanProxyInvocationHandler.this.mbeanFuture == mbeanFuture) {
                                MBeanProxyInvocationHandler.this.mbeanFuture = null;
                            }
                        }
                        return doInvoke(operationName, params, signature, true);
                    } else {
                        if (t instanceof MBeanException) {
                            // MBeanException is a wrapper around exceptions thrown by MBeans. Unwrap the exception.
                            t = t.getCause();
                        }
                        throw new CompletionException(t);
                    }
                })
                .thenCompose(Function.identity());
    }
}
