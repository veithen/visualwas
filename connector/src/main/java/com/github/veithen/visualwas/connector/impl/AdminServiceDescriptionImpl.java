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
import java.util.Map;

import com.github.veithen.visualwas.connector.description.AdminServiceDescription;
import com.github.veithen.visualwas.connector.description.OperationDescription;

final class AdminServiceDescriptionImpl implements AdminServiceDescription {
    private final Class<?> iface;
    private final Map<String,OperationHandler> operationNameToOperationHandler;
    private final Map<Method,OperationHandler> methodToOperationHandler;

    AdminServiceDescriptionImpl(Class<?> iface, Map<String,OperationHandler> operationNameToOperationHandler, Map<Method,OperationHandler> methodToOperationHandler) {
        this.iface = iface;
        this.operationNameToOperationHandler = operationNameToOperationHandler;
        this.methodToOperationHandler = methodToOperationHandler;
    }

    @Override
    public Class<?> getInterface() {
        return iface;
    }

    @Override
    public OperationDescription getOperation(String name) {
        return operationNameToOperationHandler.get(name);
    }

    Map<Method,OperationHandler> getOperationHandlers() {
        return methodToOperationHandler;
    }
}
