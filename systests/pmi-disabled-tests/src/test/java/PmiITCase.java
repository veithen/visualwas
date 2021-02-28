/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2020 Andreas Veithen
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
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.github.veithen.visualwas.client.pmi.PmiClientFeature;
import com.github.veithen.visualwas.connector.Attributes;
import com.github.veithen.visualwas.connector.Connector;
import com.github.veithen.visualwas.connector.factory.ConnectorConfiguration;
import com.github.veithen.visualwas.connector.factory.ConnectorFactory;
import com.github.veithen.visualwas.connector.security.BasicAuthCredentials;
import com.github.veithen.visualwas.connector.security.Credentials;
import com.github.veithen.visualwas.connector.transport.Endpoint;
import com.github.veithen.visualwas.connector.transport.TransportConfiguration;

public class PmiITCase {
    protected Connector connector;

    @Before
    public void initConnector() throws Exception {
        ConnectorConfiguration.Builder configBuilder = ConnectorConfiguration.custom();
        Attributes attributes =
                Attributes.builder()
                        .set(Credentials.class, new BasicAuthCredentials("wsadmin", "abcd1234"))
                        .build();
        configBuilder.setTransportConfiguration(
                TransportConfiguration.custom().disableCertificateValidation().build());
        configBuilder.addFeatures(PmiClientFeature.INSTANCE);
        Endpoint endpoint =
                new Endpoint(
                        "localhost", Integer.parseInt(System.getProperty("was.soapPort")), true);
        connector =
                ConnectorFactory.getInstance()
                        .createConnector(endpoint, configBuilder.build(), attributes);
    }

    /**
     * Regression test for issue #5.
     *
     * @throws Exception
     */
    @Test
    public void testGetServerMBean() throws Exception {
        assertThat(connector.getServerMBean()).isNotNull();
    }
}
