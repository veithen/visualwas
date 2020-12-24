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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class InterfaceFactory {
    private InterfaceFactory() {}

    public static <T> Interface<T> createInterface(Class<T> iface)
            throws InterfaceFactoryException {
        Set<Class<? extends AnnotationProcessor>> annotationProcessorClasses = new HashSet<>();
        Map<OperationKey, OperationBuilderImpl> operationBuilders = new HashMap<>();
        outer:
        for (Method method : iface.getDeclaredMethods()) {
            for (InvocationStyle invocationStyle : InvocationStyle.INSTANCES) {
                MethodInfo methodInfo = invocationStyle.getMethodInfo(method);
                if (methodInfo != null) {
                    OperationKey key =
                            new OperationKey(
                                    methodInfo.getOperationName(), methodInfo.getSignature());
                    OperationBuilderImpl operationBuilder = operationBuilders.get(key);
                    if (operationBuilder == null) {
                        operationBuilder = new OperationBuilderImpl();
                        operationBuilders.put(key, operationBuilder);
                    }
                    operationBuilder.addMethod(
                            invocationStyle, methodInfo, annotationProcessorClasses);
                    continue outer;
                }
            }
            throw new InterfaceFactoryException(
                    "Don't know what to do with method " + method.getName());
        }
        List<AnnotationProcessor> annotationProcessors =
                new ArrayList<>(annotationProcessorClasses.size());
        for (Class<? extends AnnotationProcessor> annotationProcessorClass :
                annotationProcessorClasses) {
            try {
                annotationProcessors.add(annotationProcessorClass.newInstance());
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new InterfaceFactoryException(
                        "Could not instantiate annotation processor "
                                + annotationProcessorClass.getName());
            }
        }
        Map<String, Operation> operations = new HashMap<>();
        Map<Method, InvocationHandlerDelegate> invocationHandlerDelegates = new HashMap<>();
        for (OperationBuilderImpl operationBuilder : operationBuilders.values()) {
            for (AnnotationProcessor annotationProcessor : annotationProcessors) {
                annotationProcessor.processOperation(operationBuilder);
            }
            Operation operation = operationBuilder.build();
            operations.put(operationBuilder.getName(), operation);
            for (Map.Entry<InvocationStyle, MethodInfo> entry :
                    operationBuilder.getMethods().entrySet()) {
                invocationHandlerDelegates.put(
                        entry.getValue().getMethod(),
                        new InvocationHandlerDelegate(operation, entry.getKey()));
            }
        }
        return new InterfaceImpl<>(iface, operations, invocationHandlerDelegates);
    }
}
