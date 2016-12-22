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
package com.github.veithen.visualwas.connector.impl;

import java.lang.reflect.Method;

import com.google.common.util.concurrent.ListenableFuture;

abstract class InvocationStyle {
    static final InvocationStyle[] INSTANCES = {
            new InvocationStyle() {
                @Override
                MethodInfo getMethodInfo(Method method) {
                    if (method.getReturnType() != ListenableFuture.class) {
                        return new SyncMethodInfo(method);
                    } else {
                        return null;
                    }
                }
            },
            new InvocationStyle() {
                @Override
                MethodInfo getMethodInfo(Method method) {
                    if (method.getReturnType() == ListenableFuture.class && method.getName().endsWith("Async")) {
                        return new AsyncMethodInfo(method);
                    } else {
                        return null;
                    }
                }
            }
        };

    abstract MethodInfo getMethodInfo(Method method);
}