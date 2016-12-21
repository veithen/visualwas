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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.github.veithen.visualwas.connector.Operation;
import com.github.veithen.visualwas.connector.Param;
import com.github.veithen.visualwas.connector.description.AdminServiceDescription;
import com.github.veithen.visualwas.connector.description.AdminServiceDescriptionFactory;
import com.github.veithen.visualwas.connector.description.AdminServiceDescriptionFactoryException;
import com.github.veithen.visualwas.connector.description.OperationDescription;

public final class AdminServiceDescriptionFactoryImpl extends AdminServiceDescriptionFactory {
    @Override
    public AdminServiceDescription createDescription(Class<?> iface) throws AdminServiceDescriptionFactoryException {
        Map<MethodGroupKey,MethodGroup> methodGroups = new HashMap<>();
        outer: for (Method method : iface.getDeclaredMethods()) {
            for (InvocationStyle invocationStyle : InvocationStyle.INSTANCES) {
                MethodInfo methodInfo = invocationStyle.getMethodInfo(method);
                if (methodInfo != null) {
                    MethodGroupKey key = new MethodGroupKey(methodInfo.getDefaultOperationName(), methodInfo.getSignature());
                    MethodGroup methodGroup = methodGroups.get(key);
                    if (methodGroup == null) {
                        methodGroup = new MethodGroup();
                        methodGroups.put(key, methodGroup);
                    }
                    methodGroup.add(invocationStyle, methodInfo);
                    continue outer;
                }
            }
            throw new AdminServiceDescriptionFactoryException("Don't know what to do with method " + method.getName());
        }
        Map<String,OperationDescription> operations = new HashMap<>();
        Map<Method,InvocationHandlerDelegate> invocationHandlerDelegates = new HashMap<>();
        for (MethodGroup methodGroup : methodGroups.values()) {
            Map<Class<?>, Object> adapters = new HashMap<>();
            Operation operationAnnotation = methodGroup.getAnnotation(Operation.class);
            String operationName = operationAnnotation != null && !operationAnnotation.name().isEmpty() ? operationAnnotation.name() : methodGroup.getDefaultOperationName();
            Class<?>[] signature = methodGroup.getSignature();
            int paramCount = signature.length;
            ParamHandler[] paramHandlers = new ParamHandler[paramCount];
            for (int i=0; i<paramCount; i++) {
                Class<?> type = signature[i];
                Param paramAnnotation = methodGroup.getParameterAnnotations(Param.class, i);
                if (paramAnnotation == null) {
                    throw new AdminServiceDescriptionFactoryException("Missing @Param annotation for operation " + operationName);
                }
                String name = paramAnnotation.name();
                paramHandlers[i] = new ParamHandler(name, getTypeHandler(type));
            }
            Class<?> returnType = getRawType(methodGroup.getResponseType());
            adapters.put(OperationHandler.class, new OperationHandler(operationName, operationName, operationName + "Response", paramHandlers,
                    returnType == Void.class ? null : getTypeHandler(returnType), operationAnnotation != null && operationAnnotation.suppressHeader()));
            OperationDescription operation = new OperationDescriptionImpl(adapters);
            operations.put(operationName, operation);
            for (MethodInfo methodInfo : methodGroup.getMembers()) {
                invocationHandlerDelegates.put(methodInfo.getMethod(), methodInfo.createInvocationHandlerDelegate(operation));
            }
            // TODO: check exception list; should contain IOException
        }
        return new AdminServiceDescriptionImpl(iface, operations, invocationHandlerDelegates);
    }
    
    private static Class<?> getRawType(Type type) {
        if (type instanceof ParameterizedType) {
            return (Class<?>)((ParameterizedType)type).getRawType();
        } else {
            return (Class<?>)type;
        }
    }
    
    private static TypeHandler getTypeHandler(Class<?> javaType) {
        if (javaType == Object.class) {
            return new AnyTypeHandler();
        } else {
            SimpleTypeHandler simpleTypeHandler = SimpleTypeHandler.getByJavaType(javaType);
            if (simpleTypeHandler != null) {
                return simpleTypeHandler;
            } else {
                return new ObjectHandler(javaType);
            }
        }
    }
}
