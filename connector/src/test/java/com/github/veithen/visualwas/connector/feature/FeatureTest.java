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
package com.github.veithen.visualwas.connector.feature;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.github.veithen.visualwas.connector.Connector;
import com.github.veithen.visualwas.connector.Invocation;
import com.github.veithen.visualwas.connector.factory.ConnectorConfiguration;
import com.github.veithen.visualwas.connector.factory.ConnectorFactory;
import com.github.veithen.visualwas.connector.transport.Endpoint;
import com.github.veithen.visualwas.framework.proxy.Interface;
import com.github.veithen.visualwas.framework.proxy.InterfaceFactory;
import com.github.veithen.visualwas.framework.proxy.Operation;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public class FeatureTest {
    @Test
    public void testInvocationInterceptorWithAdminServiceExtension() throws Exception {
        ConnectorConfiguration config = ConnectorConfiguration.custom().addFeatures(new Feature() {
            @Override
            public void configureConnector(Configurator configurator) {
                Interface desc = InterfaceFactory.getInstance().createDescription(DummyAdminServiceExtension.class);
                configurator.addAdminServiceInterface(desc);
                final Operation operation = desc.getOperation("echo");
                configurator.addInvocationInterceptor(new Interceptor<Invocation,Object>() {
                    public ListenableFuture<?> invoke(InvocationContext context, Invocation invocation, Handler<Invocation,Object> nextHandler) {
                        if (invocation.getOperation() == operation) {
                            return Futures.immediateFuture(invocation.getArgs()[0]);
                        } else {
                            return nextHandler.invoke(context, invocation);
                        }
                    }
                });
            }
        }).build();
        Connector connector = ConnectorFactory.getInstance().createConnector(new Endpoint("localhost", 8880, false), config, null);
        DummyAdminServiceExtension extension = connector.getAdapter(DummyAdminServiceExtension.class);
        assertNotNull(extension);
        assertEquals("test", extension.echo("test"));
    }
}
