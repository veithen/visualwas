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

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.axiom.soap.SOAPEnvelope;

import com.github.veithen.visualwas.connector.AdminService;
import com.github.veithen.visualwas.connector.Connector;
import com.github.veithen.visualwas.connector.Invocation;
import com.github.veithen.visualwas.connector.factory.Attributes;
import com.github.veithen.visualwas.connector.factory.ConnectorConfiguration;
import com.github.veithen.visualwas.connector.factory.ConnectorFactory;
import com.github.veithen.visualwas.connector.factory.DependencyUtil;
import com.github.veithen.visualwas.connector.feature.Dependencies;
import com.github.veithen.visualwas.connector.feature.Feature;
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
        InterceptorChainBuilder<Invocation,Object,Throwable> invocationInterceptors = new InterceptorChainBuilder<>();
        InterceptorChainBuilder<SOAPEnvelope,SOAPEnvelope,SOAPEnvelope> soapInterceptors = new InterceptorChainBuilder<>();
        AdaptableDelegate adaptableDelegate = new AdaptableDelegate();
        ConfiguratorImpl configurator = new ConfiguratorImpl(adminServiceInterfaces, operationHandlers, invocationInterceptors, soapInterceptors, adaptableDelegate);
        for (Feature feature : features) {
            feature.configureConnector(configurator);
        }
        configurator.release();
        AdminService adminService = (AdminService)Proxy.newProxyInstance(ConnectorFactoryImpl.class.getClassLoader(),
                adminServiceInterfaces.toArray(new Class<?>[adminServiceInterfaces.size()]),
                new AdminServiceInvocationHandler(operationHandlers,
                        invocationInterceptors.buildHandler(new MarshallingHandler(soapInterceptors.buildHandler(config.getTransportFactory().createHandler(endpoint, config.getTransportConfiguration())))),
                        config, configurator.getSerializer(),
                        new Attributes(attributes)));
        return new ConnectorImpl(adminService, adaptableDelegate);
    }
}
