package com.github.veithen.visualwas.connector.impl;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.github.veithen.visualwas.connector.Operation;
import com.github.veithen.visualwas.connector.Param;
import com.github.veithen.visualwas.connector.description.AdminServiceDescription;
import com.github.veithen.visualwas.connector.description.AdminServiceDescriptionFactory;
import com.github.veithen.visualwas.connector.description.AdminServiceDescriptionFactoryException;

public final class AdminServiceDescriptionFactoryImpl extends AdminServiceDescriptionFactory {
    @Override
    public AdminServiceDescription createDescription(Class<?> iface) throws AdminServiceDescriptionFactoryException {
        Map<Method,OperationHandler> operationHandlers = new HashMap<Method,OperationHandler>();
        for (Method method : iface.getDeclaredMethods()) {
            boolean hasConnectorException = false;
            for (Class<?> exceptionType : method.getExceptionTypes()) {
                if (exceptionType == IOException.class) {
                    hasConnectorException = true;
                    break;
                }
            }
            if (!hasConnectorException) {
                throw new AdminServiceDescriptionFactoryException("Method " + method.getName() + " doesn't declare IOException");
            }
            Class<?>[] parameterTypes = method.getParameterTypes();
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            int paramCount = parameterTypes.length;
            ParamHandler[] paramHandlers = new ParamHandler[paramCount];
            for (int i=0; i<paramCount; i++) {
                Class<?> type = parameterTypes[i];
                Annotation[] annotations = parameterAnnotations[i];
                Param paramAnnotation = null;
                for (Annotation annotation : annotations) {
                    if (annotation instanceof Param) {
                        paramAnnotation = (Param)annotation;
                        break;
                    }
                }
                if (paramAnnotation == null) {
                    throw new AdminServiceDescriptionFactoryException("Missing @Param annotation in method " + method.getName());
                }
                String name = paramAnnotation.name();
                paramHandlers[i] = new ParamHandler(name, getTypeHandler(type));
            }
            Operation operationAnnotation = method.getAnnotation(Operation.class);
            String operationName = operationAnnotation != null ? operationAnnotation.name() : method.getName();
            Class<?> returnType = method.getReturnType();
            operationHandlers.put(method, new OperationHandler(operationName, operationName, operationName + "Response", paramHandlers,
                    returnType == Void.TYPE ? null : getTypeHandler(returnType)));
        }
        return new AdminServiceDescriptionImpl(iface, operationHandlers);
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
