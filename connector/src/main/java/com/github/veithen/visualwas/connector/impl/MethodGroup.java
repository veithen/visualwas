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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.github.veithen.visualwas.connector.description.AdminServiceDescriptionFactoryException;

final class MethodGroup {
    private static final Map<Class<?>,Class<?>> wrapperTypeMap;
    
    static {
        wrapperTypeMap = new HashMap<>();
        wrapperTypeMap.put(Boolean.TYPE, Boolean.class);
        wrapperTypeMap.put(Byte.TYPE, Byte.class);
        wrapperTypeMap.put(Character.TYPE, Character.class);
        wrapperTypeMap.put(Double.TYPE, Double.class);
        wrapperTypeMap.put(Float.TYPE, Float.class);
        wrapperTypeMap.put(Integer.TYPE, Integer.class);
        wrapperTypeMap.put(Long.TYPE, Long.class);
        wrapperTypeMap.put(Short.TYPE, Short.class);
    }

    private final Map<InvocationStyle,MethodInfo> methods = new HashMap<>();
    private String defaultOperationName;
    private Class<?>[] signature;
    private Type responseType;

    void add(InvocationStyle invocationStyle, MethodInfo methodInfo) {
        if (methods.containsKey(invocationStyle)) {
            throw new AdminServiceDescriptionFactoryException("Can't have multiple methods with the same invocation style in the same method group");
        }
        if (methods.isEmpty()) {
            defaultOperationName = methodInfo.getDefaultOperationName();
            signature = methodInfo.getSignature();
            responseType = methodInfo.getResponseType();
        } else {
            if (!defaultOperationName.equals(methodInfo.getDefaultOperationName())) {
                throw new AdminServiceDescriptionFactoryException("Inconsistent default operation names in method group");
            }
            if (!Arrays.equals(signature, methodInfo.getSignature())) {
                throw new AdminServiceDescriptionFactoryException("Inconsistent method signatures in method group");
            }
            Type newResponseType = methodInfo.getResponseType();
            if (!responseType.equals(newResponseType)) {
                if (responseType.equals(wrapperTypeMap.get(newResponseType))) {
                    responseType = newResponseType;
                } else if (!(newResponseType.equals(wrapperTypeMap.get(responseType)))) {
                    throw new AdminServiceDescriptionFactoryException("Inconsistent response types in method group: " + responseType + ", " + newResponseType);
                }
            }
        }
        methods.put(invocationStyle, methodInfo);
    }

    Collection<MethodInfo> getMembers() {
        return methods.values();
    }

    String getDefaultOperationName() {
        return defaultOperationName;
    }

    Class<?>[] getSignature() {
        return signature;
    }

    Type getResponseType() {
        return responseType;
    }

    <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        T result = null;
        for (MethodInfo methodInfo : methods.values()) {
            T annotation = methodInfo.getMethod().getAnnotation(annotationClass);
            if (annotation != null) {
                if (result != null) {
                    throw new AdminServiceDescriptionFactoryException("Duplicate " + annotationClass.getName() + " annotation for method " + methodInfo.getDefaultOperationName());
                }
                result = annotation;
            }
        }
        return result;
    }

    <T extends Annotation> T getParameterAnnotations(Class<T> annotationClass, int index) {
        T result = null;
        for (MethodInfo methodInfo : methods.values()) {
            T annotation = null;
            for (Annotation candidate : methodInfo.getMethod().getParameterAnnotations()[index]) {
                if (annotationClass.isInstance(candidate)) {
                    annotation = annotationClass.cast(candidate);
                    break;
                }
            }
            if (annotation != null) {
                if (result != null) {
                    throw new AdminServiceDescriptionFactoryException("Duplicate " + annotationClass.getName() + " annotation for parameter " + index + " of method " + methodInfo.getDefaultOperationName());
                }
                result = annotation;
            }
        }
        return result;
    }
}
