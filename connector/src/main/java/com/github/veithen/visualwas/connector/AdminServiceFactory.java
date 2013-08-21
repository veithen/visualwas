package com.github.veithen.visualwas.connector;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.veithen.visualwas.connector.feature.Feature;
import com.github.veithen.visualwas.connector.security.Credentials;
import com.github.veithen.visualwas.connector.transport.Endpoint;

public final class AdminServiceFactory {
    private static AdminServiceFactory instance;
    
    private final Map<Method,OperationHandler> operationHandlers = new HashMap<Method,OperationHandler>();
    
    private AdminServiceFactory() {
        for (Method method : AdminService.class.getDeclaredMethods()) {
            boolean hasConnectorException = false;
            for (Class<?> exceptionType : method.getExceptionTypes()) {
                if (exceptionType == IOException.class) {
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
            operationHandlers.put(method, new OperationHandler(methodName, methodName, methodName + "Response", paramHandlers, getTypeHandler(method.getReturnType())));
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
    
    public AdminService createAdminService(Endpoint endpoint, Credentials credentials, ConnectorConfiguration config) {
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        ClassMapper classMapper = new ClassMapper();
        AdaptableDelegate adaptableDelegate = new AdaptableDelegate();
        ConfiguratorImpl configurator = new ConfiguratorImpl(interceptors, classMapper, adaptableDelegate);
        BaseFeature.INSTANCE.configureConnector(configurator);
        for (Feature feature : config.getFeatures()) {
            feature.configureConnector(configurator);
        }
        configurator.release();
        if (credentials != null) {
            interceptors.add(credentials.createInterceptor());
        }
        AdminService adminService = (AdminService)Proxy.newProxyInstance(AdminServiceFactory.class.getClassLoader(), new Class<?>[] { AdminService.class },
                new AdminServiceInvocationHandler(operationHandlers, interceptors.toArray(new Interceptor[interceptors.size()]),
                        config.getTransportFactory().createTransport(endpoint, config.getTransportConfiguration()), config, credentials, adaptableDelegate, classMapper));
        adaptableDelegate.setAdminService(adminService);
        return adminService;
    }
}
