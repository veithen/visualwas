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
package com.github.veithen.visualwas.connector.mapped;

import com.github.veithen.visualwas.connector.AdminService;
import com.github.veithen.visualwas.connector.Callback;
import com.github.veithen.visualwas.connector.Handler;
import com.github.veithen.visualwas.connector.Invocation;
import com.github.veithen.visualwas.connector.description.OperationDescription;
import com.github.veithen.visualwas.connector.feature.Interceptor;
import com.github.veithen.visualwas.connector.feature.InvocationContext;

final class InvocationInterceptor implements Interceptor<Invocation,Object,Throwable> {
    private static final OperationDescription invokeOperation = AdminService.DESCRIPTION.getOperation("invoke");
    
    private final ClassMapper classMapper;

    InvocationInterceptor(ClassMapper classMapper) {
        this.classMapper = classMapper;
    }

    @Override
    public void invoke(InvocationContext context, Invocation invocation, Callback<Object,Throwable> callback, Handler<Invocation,Object,Throwable> nextHandler) {
        if (invocation.getOperation() == invokeOperation) {
            Object[] args = invocation.getArgs();
            String[] signature = (String[])args[3];
            if (signature != null) {
                boolean cloned = false;
                for (int i=0; i<signature.length; i++) {
                    String originalClass = classMapper.getOriginalClass(signature[i]);
                    if (originalClass != null) {
                        if (!cloned) {
                            args[3] = signature = signature.clone();
                            cloned = true;
                        }
                        signature[i] = originalClass;
                    }
                }
            }
        }
        nextHandler.invoke(context, invocation, callback);
    }
}
