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
package com.github.veithen.visualwas.framework.proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class InterfaceFactory {
    public static Interface createInterface(Class<?> iface) throws InterfaceFactoryException {
        Set<Class<? extends AnnotationProcessor>> annotationProcessorClasses = new HashSet<>();
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
                    methodGroup.add(invocationStyle, methodInfo, annotationProcessorClasses);
                    continue outer;
                }
            }
            throw new InterfaceFactoryException("Don't know what to do with method " + method.getName());
        }
        List<AnnotationProcessor> annotationProcessors = new ArrayList<>(annotationProcessorClasses.size());
        for (Class<? extends AnnotationProcessor> annotationProcessorClass : annotationProcessorClasses) {
            try {
                annotationProcessors.add(annotationProcessorClass.newInstance());
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new InterfaceFactoryException("Could not instantiate annotation processor " + annotationProcessorClass.getName());
            }
        }
        Map<String,Operation> operations = new HashMap<>();
        Map<Method,InvocationHandlerDelegate> invocationHandlerDelegates = new HashMap<>();
        for (MethodGroup methodGroup : methodGroups.values()) {
            for (AnnotationProcessor annotationProcessor : annotationProcessors) {
                annotationProcessor.processOperation(methodGroup);
            }
            Operation operation = methodGroup.build();
            operations.put(methodGroup.getOperationName(), operation);
            for (MethodInfo methodInfo : methodGroup.getMembers()) {
                invocationHandlerDelegates.put(methodInfo.getMethod(), methodInfo.createInvocationHandlerDelegate(operation));
            }
        }
        return new InterfaceImpl(iface, operations, invocationHandlerDelegates);
    }
}
