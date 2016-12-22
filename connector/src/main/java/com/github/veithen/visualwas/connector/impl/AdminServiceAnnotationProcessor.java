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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.github.veithen.visualwas.connector.Operation;
import com.github.veithen.visualwas.connector.Param;
import com.github.veithen.visualwas.framework.proxy.AnnotationProcessor;
import com.github.veithen.visualwas.framework.proxy.InterfaceFactoryException;
import com.github.veithen.visualwas.framework.proxy.OperationBuilder;

public class AdminServiceAnnotationProcessor implements AnnotationProcessor {
    @Override
    public void processOperation(OperationBuilder operationBuilder) {
        Operation operationAnnotation = operationBuilder.getOperationAnnotation(Operation.class);
        String operationName = operationAnnotation != null && !operationAnnotation.name().isEmpty() ? operationAnnotation.name() : operationBuilder.getOperationName();
        Class<?>[] signature = operationBuilder.getSignature();
        int paramCount = signature.length;
        ParamHandler[] paramHandlers = new ParamHandler[paramCount];
        for (int i=0; i<paramCount; i++) {
            Class<?> type = signature[i];
            Param paramAnnotation = operationBuilder.getParameterAnnotation(Param.class, i);
            if (paramAnnotation == null) {
                throw new InterfaceFactoryException("Missing @Param annotation for operation " + operationName);
            }
            String name = paramAnnotation.name();
            paramHandlers[i] = new ParamHandler(name, getTypeHandler(type));
        }
        Class<?> returnType = getRawType(operationBuilder.getResponseType());
        operationBuilder.addAdapter(OperationHandler.class, new OperationHandler(operationName, operationName, operationName + "Response", paramHandlers,
                returnType == Void.class ? null : getTypeHandler(returnType), operationAnnotation != null && operationAnnotation.suppressHeader()));
        // TODO: check exception list; should contain IOException
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
