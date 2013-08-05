package com.github.veithen.visualwas.connector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import com.github.veithen.visualwas.connector.transport.Transport;

public final class AdminServiceFactory {
    private static AdminServiceFactory instance;
    
    private final Map<Method,OperationHandler> operationHandlers = new HashMap<Method,OperationHandler>();
    
    private AdminServiceFactory() {
        for (Method method : AdminService.class.getMethods()) {
            boolean hasConnectorException = false;
            for (Class<?> exceptionType : method.getExceptionTypes()) {
                if (exceptionType == ConnectorException.class) {
                    hasConnectorException = true;
                    break;
                }
            }
            if (!hasConnectorException) {
                throw new Error("Method " + method.getName() + " doesn't declare ConnectorException");
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
                    throw new Error("Missing @Param annotation in method " + method.getName());
                }
                String name = paramAnnotation.name();
                paramHandlers[i] = new ParamHandler(name, getTypeHandler(type));
            }
            String methodName = method.getName();
            operationHandlers.put(method, new OperationHandler(methodName, methodName + "Response", paramHandlers, getTypeHandler(method.getReturnType())));
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
    
    public synchronized static AdminServiceFactory getInstance() {
        if (instance == null) {
            instance = new AdminServiceFactory();
        }
        return instance;
    }
    
    public AdminService createAdminService(Interceptor[] interceptors, Transport transport) {
        return (AdminService)Proxy.newProxyInstance(AdminServiceFactory.class.getClassLoader(), new Class<?>[] { AdminService.class }, new AdminServiceInvocationHandler(operationHandlers, interceptors, transport));
    }
}
