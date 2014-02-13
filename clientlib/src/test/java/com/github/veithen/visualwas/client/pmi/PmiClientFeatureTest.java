/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2014 Andreas Veithen
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
package com.github.veithen.visualwas.client.pmi;

import org.junit.Test;

import com.github.veithen.visualwas.connector.Connector;
import com.github.veithen.visualwas.connector.transport.dummy.DictionaryRequestMatcher;
import com.github.veithen.visualwas.connector.transport.dummy.DummyTransport;

public class PmiClientFeatureTest {
    @Test
    public void testGetStatObject() throws Exception {
        DummyTransport transport = new DummyTransport(new DictionaryRequestMatcher());
        transport.addExchanges(PmiClientFeatureTest.class, "getServerMBean", "queryNames", "invoke-getStatsObject");
        Connector connector = transport.createConnector(PmiClientFeature.INSTANCE);
        Perf perf = connector.getAdapter(Perf.class);
        Stats stats = perf.getStatsObject(new MBeanStatDescriptor(connector.getServerMBean(), "threadPoolModule", "WebContainer"), false);
    }
}
