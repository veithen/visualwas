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

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public abstract class InvocationStyle {
    public static final InvocationStyle SYNC = new InvocationStyle() {
        @Override
        MethodInfo getMethodInfo(Method method) {
            if (method.getReturnType() != CompletableFuture.class) {
                return new SyncMethodInfo(method);
            } else {
                return null;
            }
        }

        @Override
        Object invoke(InvocationTarget target, Operation operation, Object[] args) throws Throwable {
            try {
                return target.invoke(new Invocation(operation, this, args)).get();
            } catch (ExecutionException ex) {
                throw ex.getCause();
            }
        }
    };
    
    public static final InvocationStyle ASYNC = new InvocationStyle() {
        @Override
        MethodInfo getMethodInfo(Method method) {
            if (method.getReturnType() == CompletableFuture.class && method.getName().endsWith("Async")) {
                return new AsyncMethodInfo(method);
            } else {
                return null;
            }
        }

        @Override
        Object invoke(InvocationTarget target, Operation operation, Object[] args) throws Throwable {
            return target.invoke(new Invocation(operation, this, args));
        }
    };

    static final InvocationStyle[] INSTANCES = { SYNC, ASYNC };

    abstract MethodInfo getMethodInfo(Method method);
    abstract Object invoke(InvocationTarget target, Operation operation, Object[] args) throws Throwable;
}
