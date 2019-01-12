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
package com.github.veithen.visualwas.framework.proxy;

import java.lang.reflect.Type;
import java.util.Map;

final class OperationImpl implements Operation {
    private final String name;
    private final Class<?>[] signature;
    private final Type responseType;
    private final Class<?>[] exceptionTypes;
    private final Map<Class<?>,Object> adapters;

    OperationImpl(String name, Class<?>[] signature, Type responseType, Class<?>[] exceptionTypes, Map<Class<?>, Object> adapters) {
        this.name = name;
        this.signature = signature;
        this.responseType = responseType;
        this.exceptionTypes = exceptionTypes;
        this.adapters = adapters;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<?>[] getSignature() {
        return signature.clone();
    }

    @Override
    public Type getResponseType() {
        return responseType;
    }

    @Override
    public Class<?>[] getExceptionTypes() {
        return exceptionTypes.clone();
    }

    @Override
    public <T> T getAdapter(Class<T> clazz) {
        return clazz.cast(adapters.get(clazz));
    }
}
