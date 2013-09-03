package com.github.veithen.visualwas.connector.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.veithen.visualwas.connector.AdminService;
import com.github.veithen.visualwas.connector.Connector;
import com.github.veithen.visualwas.connector.factory.Attributes;
import com.github.veithen.visualwas.connector.factory.ConnectorConfiguration;
import com.github.veithen.visualwas.connector.factory.ConnectorFactory;
import com.github.veithen.visualwas.connector.factory.DependencyUtil;
import com.github.veithen.visualwas.connector.feature.Dependencies;
import com.github.veithen.visualwas.connector.feature.Feature;
import com.github.veithen.visualwas.connector.feature.Interceptor;
import com.github.veithen.visualwas.connector.transport.Endpoint;

public final class ConnectorFactoryImpl extends ConnectorFactory {
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
        Set<Class<?>> adminServiceInterfaces = new HashSet<Class<?>>();
        Map<Method,OperationHandler> operationHandlers = new HashMap<Method,OperationHandler>(((AdminServiceDescriptionImpl)AdminService.DESCRIPTION).getOperationHandlers());
        adminServiceInterfaces.add(AdminService.class);
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        AdaptableDelegate adaptableDelegate = new AdaptableDelegate();
        ConfiguratorImpl configurator = new ConfiguratorImpl(adminServiceInterfaces, operationHandlers, interceptors, adaptableDelegate);
        for (Feature feature : features) {
            feature.configureConnector(configurator);
        }
        configurator.release();
        AdminService adminService = (AdminService)Proxy.newProxyInstance(ConnectorFactoryImpl.class.getClassLoader(),
                adminServiceInterfaces.toArray(new Class<?>[adminServiceInterfaces.size()]),
                new AdminServiceInvocationHandler(operationHandlers, interceptors.toArray(new Interceptor[interceptors.size()]),
                        config.getTransportFactory().createTransport(endpoint, config.getTransportConfiguration()), config, configurator.getSerializer(),
                        new Attributes(attributes)));
        return new ConnectorImpl(adminService, adaptableDelegate);
    }
}
