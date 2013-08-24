package com.github.veithen.visualwas.connector;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.veithen.visualwas.connector.feature.Dependencies;
import com.github.veithen.visualwas.connector.feature.Feature;
import com.github.veithen.visualwas.connector.transport.Endpoint;

public final class ConnectorFactory {
    private static ConnectorFactory instance;
    
    private final Map<Method,OperationHandler> operationHandlers = new HashMap<Method,OperationHandler>();
    
    private ConnectorFactory() {
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
    
    public synchronized static ConnectorFactory getInstance() {
        if (instance == null) {
            instance = new ConnectorFactory();
        }
        return instance;
    }
    
    public Connector createConnector(Endpoint endpoint, ConnectorConfiguration config, Attributes attributes) {
        List<Feature> features = new ArrayList<Feature>(config.getFeatures());
        if (attributes != null) {
            for (Class<?> key : attributes.keySet()) {
                Dependencies ann = key.getAnnotation(Dependencies.class);
                if (ann != null) {
                    DependencyUtil.resolveDependencies(ann, null, features);
                }
            }
        }
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        AdaptableDelegate adaptableDelegate = new AdaptableDelegate();
        ConfiguratorImpl configurator = new ConfiguratorImpl(interceptors, adaptableDelegate);
        for (Feature feature : features) {
            feature.configureConnector(configurator);
        }
        configurator.release();
        AdminService adminService = (AdminService)Proxy.newProxyInstance(ConnectorFactory.class.getClassLoader(), new Class<?>[] { AdminService.class },
                new AdminServiceInvocationHandler(operationHandlers, interceptors.toArray(new Interceptor[interceptors.size()]),
                        config.getTransportFactory().createTransport(endpoint, config.getTransportConfiguration()), config, configurator.getSerializer(),
                        new Attributes(attributes)));
        return new ConnectorImpl(adminService, adaptableDelegate);
    }
}
