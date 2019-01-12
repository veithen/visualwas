/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2019 Andreas Veithen
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

import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.CompletableFuture;

import org.junit.Test;

import com.github.veithen.visualwas.connector.AdminService;
import com.github.veithen.visualwas.connector.Connector;
import com.github.veithen.visualwas.connector.ConnectorException;
import com.github.veithen.visualwas.connector.factory.ConnectorConfiguration;
import com.github.veithen.visualwas.connector.factory.ConnectorFactory;
import com.github.veithen.visualwas.connector.transport.Endpoint;

public class ContextPopulatingInterceptorTest {
    private class DummyContextKey {}

    /**
     * Tests that a failure to retrieve the context value results in a meaningful exception
     * (instead of {@link UndeclaredThrowableException}).
     * 
     * @throws Exception
     */
    @Test(expected=ConnectorException.class)
    public void testException() throws Exception {
        ConnectorConfiguration config = ConnectorConfiguration.custom().addFeatures(new Feature() {
            @Override
            public void configureConnector(Configurator configurator) {
                configurator.addInvocationInterceptor(new ContextPopulatingInterceptor<DummyContextKey>(DummyContextKey.class) {
                    @Override
                    protected CompletableFuture<DummyContextKey> produceValue(AdminService adminService) {
                        CompletableFuture<DummyContextKey> future = new CompletableFuture<>();
                        future.completeExceptionally(new Exception());
                        return future;
                    }
                });
            }
        }).build();
        Connector connector = ConnectorFactory.getInstance().createConnector(new Endpoint("localhost", 8880, false), config, null);
        connector.getServerMBean();
    }
}
