/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2018 Andreas Veithen
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
package com.github.veithen.visualwas.connector.feature;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import com.github.veithen.visualwas.connector.AdminService;
import com.github.veithen.visualwas.connector.ConnectorException;
import com.github.veithen.visualwas.framework.proxy.Invocation;

public abstract class ContextPopulatingInterceptor<T> implements Interceptor<Invocation,Object> {
    private final Class<T> type;
    private CompletableFuture<T> future;

    public ContextPopulatingInterceptor(Class<T> type) {
        this.type = type;
    }

    @Override
    public final CompletableFuture<?> invoke(final InvocationContext context, final Invocation request,
            final Handler<Invocation, Object> nextHandler) {
        CompletableFuture<T> future;
        synchronized (this) {
            future = this.future;
            if (future == null) {
                this.future = future = produceValue(context.getAdminService(nextHandler)).exceptionally(t -> {
                    if (t instanceof CompletionException) {
                        t = t.getCause();
                    }
                    // If it's an IOException, assume it's a low level problem independent of the
                    // operation being invoked and simply propagate the exception. Otherwise it's likely
                    // a problem specific to the operation invoked to produce the value. In that case
                    // wrap the exception. This also prevents UndeclaredThrowableExceptions from being
                    // thrown at the caller of the proxy.
                    if (!(t instanceof IOException)) {
                        t = new ConnectorException(
                                String.format("Failed to populate context attribute with type %s", type.getName()), t);
                    }
                    throw new CompletionException(t);
                });
                future.whenComplete((result, t) -> {
                    if (t != null) {
                        // Remove the future so that subsequent invocations will retry
                        synchronized (ContextPopulatingInterceptor.this) {
                            ContextPopulatingInterceptor.this.future = null;
                        }
                    }
                });
            }
        }
        return future.thenCompose(value -> {
            // TODO: should the context really be mutable?
            context.setAttribute(type, value);
            return nextHandler.invoke(context, request);
        });
    }

    protected abstract CompletableFuture<T> produceValue(AdminService adminService);
}
