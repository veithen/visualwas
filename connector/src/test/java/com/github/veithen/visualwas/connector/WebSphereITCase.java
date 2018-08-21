/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2018 Andreas Veithen
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
package com.github.veithen.visualwas.connector;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

import javax.management.ObjectName;

import org.junit.Test;

import com.github.veithen.visualwas.connector.factory.Attributes;
import com.github.veithen.visualwas.connector.factory.ConnectorConfiguration;
import com.github.veithen.visualwas.connector.factory.ConnectorFactory;
import com.github.veithen.visualwas.connector.mapped.ClassMappingFeature;
import com.github.veithen.visualwas.connector.mapped.SOAPException;
import com.github.veithen.visualwas.connector.security.BasicAuthCredentials;
import com.github.veithen.visualwas.connector.security.Credentials;
import com.github.veithen.visualwas.connector.transport.Endpoint;
import com.github.veithen.visualwas.connector.transport.TransportConfiguration;

public class WebSphereITCase {
    @Test
    public void testInvalidCredentials() throws Exception {
        ConnectorConfiguration.Builder configBuilder = ConnectorConfiguration.custom();
        Attributes attributes = new Attributes();
        attributes.set(Credentials.class, new BasicAuthCredentials("wsadmin", "invalid"));
        configBuilder.setTransportConfiguration(TransportConfiguration.custom().disableCertificateValidation().build());
        configBuilder.addFeatures(ClassMappingFeature.INSTANCE);
        Endpoint endpoint = new Endpoint("localhost", Integer.parseInt(System.getProperty("was.soapPort")), true);
        Connector connector = ConnectorFactory.getInstance().createConnector(endpoint, configBuilder.build(), attributes);
        try {
            ObjectName serverMBean = connector.getServerMBean();
            connector.getAttribute(serverMBean, "pid");
            fail("Expected exception");
        } catch (ConnectorException ex) {
            assertThat(ex.getCause()).isInstanceOf(SOAPException.class);
        }
    }
}
