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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class OperationBuilderImpl implements OperationBuilder {
    private static final Map<Class<?>, Class<?>> wrapperTypeMap;

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

    private final Map<InvocationStyle, MethodInfo> methods = new HashMap<>();
    private String name;
    private Class<?>[] signature;
    private Type responseType;
    private Class<?>[] exceptionTypes;
    private Map<Class<?>, Annotation> annotations = new HashMap<>();
    private List<Map<Class<?>, Annotation>> paramAnnotations;
    private final Map<Class<?>, Object> adapters = new HashMap<>();

    void addMethod(
            InvocationStyle invocationStyle,
            MethodInfo methodInfo,
            Set<Class<? extends AnnotationProcessor>> annotationProcessorClasses) {
        if (methods.containsKey(invocationStyle)) {
            throw new InterfaceFactoryException(
                    "Can't have multiple methods with the same invocation style in the same method group");
        }
        if (methods.isEmpty()) {
            name = methodInfo.getOperationName();
            signature = methodInfo.getSignature();
            responseType = methodInfo.getResponseType();
            paramAnnotations = new ArrayList<>(signature.length);
            for (int i = 0; i < signature.length; i++) {
                paramAnnotations.add(new HashMap<Class<?>, Annotation>());
            }
        } else {
            if (!name.equals(methodInfo.getOperationName())) {
                throw new InterfaceFactoryException("Inconsistent operation names in method group");
            }
            if (!Arrays.equals(signature, methodInfo.getSignature())) {
                throw new InterfaceFactoryException(
                        "Inconsistent method signatures in method group");
            }
            Type newResponseType = methodInfo.getResponseType();
            if (!responseType.equals(newResponseType)) {
                if (responseType.equals(wrapperTypeMap.get(newResponseType))) {
                    responseType = newResponseType;
                } else if (!(newResponseType.equals(wrapperTypeMap.get(responseType)))) {
                    throw new InterfaceFactoryException(
                            "Inconsistent response types in method group: "
                                    + responseType
                                    + ", "
                                    + newResponseType);
                }
            }
        }
        Class<?>[] exceptionTypes = methodInfo.getExceptionTypes();
        if (exceptionTypes != null) {
            if (this.exceptionTypes != null) {
                // TODO
                throw new UnsupportedOperationException();
            } else {
                this.exceptionTypes = exceptionTypes;
            }
        }
        Method method = methodInfo.getMethod();
        for (Annotation annotation : method.getAnnotations()) {
            Class<?> annotationType = annotation.annotationType();
            OperationAnnotation metaAnnotation =
                    annotationType.getAnnotation(OperationAnnotation.class);
            if (metaAnnotation != null) {
                annotationProcessorClasses.add(
                        ((OperationAnnotation) metaAnnotation).annotationProcessor());
                if (annotations.containsKey(annotationType)) {
                    throw new InterfaceFactoryException(
                            "Duplicate "
                                    + annotationType.getName()
                                    + " annotation for operation "
                                    + methodInfo.getOperationName());
                }
                annotations.put(annotationType, annotation);
            }
        }
        Annotation[][] methodParamAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < signature.length; i++) {
            Map<Class<?>, Annotation> annotations = paramAnnotations.get(i);
            for (Annotation annotation : methodParamAnnotations[i]) {
                Class<?> annotationType = annotation.annotationType();
                ParamAnnotation metaAnnotation =
                        annotationType.getAnnotation(ParamAnnotation.class);
                if (metaAnnotation != null) {
                    annotationProcessorClasses.add(
                            ((ParamAnnotation) metaAnnotation).annotationProcessor());
                    if (annotations.containsKey(annotationType)) {
                        throw new InterfaceFactoryException(
                                "Duplicate "
                                        + annotationType.getName()
                                        + " annotation for parameter "
                                        + i
                                        + " of operation "
                                        + methodInfo.getOperationName());
                    }
                    annotations.put(annotationType, annotation);
                }
            }
        }
        methods.put(invocationStyle, methodInfo);
    }

    Map<InvocationStyle, MethodInfo> getMethods() {
        return methods;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<?>[] getSignature() {
        return signature;
    }

    @Override
    public Type getResponseType() {
        return responseType;
    }

    @Override
    public Class<?>[] getExceptionTypes() {
        return exceptionTypes;
    }

    @Override
    public <T extends Annotation> T getOperationAnnotation(Class<T> annotationClass) {
        return annotationClass.cast(annotations.get(annotationClass));
    }

    @Override
    public <T extends Annotation> T getParameterAnnotation(Class<T> annotationClass, int index) {
        return annotationClass.cast(paramAnnotations.get(index).get(annotationClass));
    }

    @Override
    public <T> void addAdapter(Class<T> type, T instance) {
        adapters.put(type, instance);
    }

    Operation build() {
        return new OperationImpl(name, signature, responseType, exceptionTypes, adapters);
    }
}
