package com.github.veithen.visualwas.connector.impl;

import java.lang.reflect.Method;
import java.util.Map;

import com.github.veithen.visualwas.connector.description.AdminServiceDescription;

final class AdminServiceDescriptionImpl implements AdminServiceDescription {
    private final Class<?> iface;
    private final Map<Method,OperationHandler> operationHandlers;

    AdminServiceDescriptionImpl(Class<?> iface, Map<Method, OperationHandler> operationHandlers) {
        this.iface = iface;
        this.operationHandlers = operationHandlers;
    }

    @Override
    public Class<?> getInterface() {
        return iface;
    }

    Map<Method, OperationHandler> getOperationHandlers() {
        return operationHandlers;
    }
}
