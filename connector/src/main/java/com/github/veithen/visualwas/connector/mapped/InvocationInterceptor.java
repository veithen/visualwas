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
package com.github.veithen.visualwas.connector.mapped;

import com.github.veithen.visualwas.connector.Invocation;
import com.github.veithen.visualwas.connector.feature.Handler;
import com.github.veithen.visualwas.connector.feature.Interceptor;
import com.github.veithen.visualwas.connector.feature.InvocationContext;
import com.google.common.util.concurrent.ListenableFuture;

final class InvocationInterceptor implements Interceptor<Invocation,Object> {
    private final ClassMapper classMapper;

    InvocationInterceptor(ClassMapper classMapper) {
        this.classMapper = classMapper;
    }

    @Override
    public ListenableFuture<?> invoke(InvocationContext context, Invocation invocation, Handler<Invocation,Object> nextHandler) {
        if (invocation.getOperation().getName().equals("invoke")) {
            Object[] args = invocation.getArgs();
            String[] signature = (String[])args[3];
            if (signature != null) {
                boolean cloned = false;
                for (int i=0; i<signature.length; i++) {
                    String localClass = signature[i];
                    String remoteClass = classMapper.toRemoteClass(localClass);
                    if (remoteClass != localClass) {
                        if (!cloned) {
                            args[3] = signature = signature.clone();
                            cloned = true;
                        }
                        signature[i] = remoteClass;
                    }
                }
            }
        }
        return nextHandler.invoke(context, invocation);
    }
}
