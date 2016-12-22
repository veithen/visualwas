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

import java.util.ArrayList;
import java.util.List;

import org.apache.axiom.soap.SOAPEnvelope;

import com.github.veithen.visualwas.connector.AdminService;
import com.github.veithen.visualwas.connector.Connector;
import com.github.veithen.visualwas.connector.Invocation;
import com.github.veithen.visualwas.connector.description.Interface;
import com.github.veithen.visualwas.connector.factory.Attributes;
import com.github.veithen.visualwas.connector.factory.ConnectorConfiguration;
import com.github.veithen.visualwas.connector.factory.ConnectorFactory;
import com.github.veithen.visualwas.connector.factory.DependencyUtil;
import com.github.veithen.visualwas.connector.feature.Dependencies;
import com.github.veithen.visualwas.connector.feature.Feature;
import com.github.veithen.visualwas.connector.feature.SOAPResponse;
import com.github.veithen.visualwas.connector.transport.Endpoint;
import com.github.veithen.visualwas.connector.transport.TransportConfiguration;

public final class ConnectorFactoryImpl extends ConnectorFactory {
    public Connector createConnector(Endpoint endpoint, final ConnectorConfiguration config, Attributes attributes) {
        List<Feature> features = new ArrayList<Feature>(config.getFeatures());
        if (attributes != null) {
            for (Class<?> key : attributes.keySet()) {
                Dependencies ann = key.getAnnotation(Dependencies.class);
                if (ann != null) {
                    DependencyUtil.resolveDependencies(ann, null, features);
                }
            }
        }
        List<Interface> adminServiceInterfaces = new ArrayList<>();
        adminServiceInterfaces.add(AdminService.DESCRIPTION);
        InterceptorChainBuilder<Invocation,Object> invocationInterceptors = new InterceptorChainBuilder<>();
        InterceptorChainBuilder<SOAPEnvelope,SOAPResponse> soapInterceptors = new InterceptorChainBuilder<>();
        AdaptableDelegate adaptableDelegate = new AdaptableDelegate();
        final ConfiguratorImpl configurator = new ConfiguratorImpl(adminServiceInterfaces, invocationInterceptors, soapInterceptors, adaptableDelegate);
        for (Feature feature : features) {
            feature.configureConnector(configurator);
        }
        configurator.release();
        final AdminServiceFactory adminServiceFactory = new AdminServiceFactory(
                adminServiceInterfaces.toArray(new Interface[adminServiceInterfaces.size()]));
        final Attributes initialAttributes = new Attributes(attributes);
        initialAttributes.set(TransportConfiguration.class, config.getTransportConfiguration());
        AdminService adminService = adminServiceFactory.create(
                new InvocationContextProvider() {
                    @Override
                    public InvocationContextImpl get() {
                        return new InvocationContextImpl(config, adminServiceFactory, configurator.getSerializer(), initialAttributes);
                    }
                },
                invocationInterceptors.buildHandler(new MarshallingHandler(soapInterceptors.buildHandler(config.getTransportFactory().createHandler(endpoint, config.getTransportConfiguration())))));
        return new ConnectorImpl(adminService, adaptableDelegate);
    }
}
