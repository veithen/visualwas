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
package com.github.veithen.visualwas.connector.proxy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.net.SocketException;

import org.junit.jupiter.api.Test;

import com.github.veithen.visualwas.connector.Connector;
import com.github.veithen.visualwas.connector.transport.dummy.DictionaryRequestMatcher;
import com.github.veithen.visualwas.connector.transport.dummy.DummyTransport;
import com.github.veithen.visualwas.connector.transport.dummy.SequencedRequestMatcher;
import com.github.veithen.visualwas.connector.transport.dummy.TransportErrorResponse;

public class ProxyConfiguratorTest {
    @Test
    public void test() throws Exception {
        DummyTransport transport = new DummyTransport(new SequencedRequestMatcher());
        transport.addExchanges(
                ProxyConfiguratorTest.class,
                "queryNames-ApplicationManager",
                "invoke-stopApplication");
        Connector connector = transport.createConnector(ApplicationManagerFeature.INSTANCE);
        ApplicationManager appman = connector.getAdapter(ApplicationManager.class);
        appman.stopApplication("ibmasyncrsp");
    }

    @Test
    public void testTransportError() throws Exception {
        DictionaryRequestMatcher requestMatcher = new DictionaryRequestMatcher();
        IOException exception = new SocketException("Connection refused");
        requestMatcher.setDefaultResponse(new TransportErrorResponse(exception));
        DummyTransport transport = new DummyTransport(requestMatcher);
        transport.addExchange(ProxyConfiguratorTest.class, "queryNames-ApplicationManager");
        Connector connector = transport.createConnector(ApplicationManagerFeature.INSTANCE);
        ApplicationManager appman = connector.getAdapter(ApplicationManager.class);
        IOException ex = assertThrows(IOException.class, () -> appman.stopApplication("test"));
        assertThat(ex).isSameAs(exception);
    }
}
