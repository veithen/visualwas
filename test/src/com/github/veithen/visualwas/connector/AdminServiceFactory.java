package com.github.veithen.visualwas.connector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public final class AdminServiceFactory {
    private static AdminServiceFactory instance;
    
    private final Map<Method,OperationHandler> operationHandlers = new HashMap<>();
    
    private AdminServiceFactory() {
        for (Method method : AdminService.class.getMethods()) {
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
    
    public AdminService createAdminService() {
        return (AdminService)Proxy.newProxyInstance(AdminServiceFactory.class.getClassLoader(), new Class<?>[] { AdminService.class }, new AdminServiceInvocationHandler(operationHandlers));
    }
}
