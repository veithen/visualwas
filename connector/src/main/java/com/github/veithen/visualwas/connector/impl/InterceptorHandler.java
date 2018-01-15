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
package com.github.veithen.visualwas.connector.impl;

import com.github.veithen.visualwas.connector.feature.Handler;
import com.github.veithen.visualwas.connector.feature.Interceptor;
import com.github.veithen.visualwas.connector.feature.InvocationContext;
import com.google.common.util.concurrent.ListenableFuture;

final class InterceptorHandler<S,T> implements Handler<S,T> {
    private final Interceptor<S,T> interceptor;
    private final Handler<S,T> nextHandler;

    InterceptorHandler(Interceptor<S,T> interceptor, Handler<S,T> nextHandler) {
        this.interceptor = interceptor;
        this.nextHandler = nextHandler;
    }

    @Override
    public ListenableFuture<? extends T> invoke(InvocationContext context, S request) {
        return interceptor.invoke(context, request, nextHandler);
    }
}
