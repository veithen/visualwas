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
package com.github.veithen.visualwas.connector.federation;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Set;

import javax.management.ObjectName;

import org.junit.Test;

import com.github.veithen.visualwas.connector.Connector;
import com.github.veithen.visualwas.connector.transport.dummy.DictionaryRequestMatcher;
import com.github.veithen.visualwas.connector.transport.dummy.DummyTransport;

public class DisableFederationFeatureTest {
    @Test
    public void testQueryNames() throws Exception {
        DummyTransport transport = new DummyTransport(new DictionaryRequestMatcher());
        transport.addExchanges(DisableFederationFeatureTest.class, "getServerMBean", "queryNames-allRoutable", "queryNames-java-lang", "queryNames-JMImplementation");
        Connector connector = transport.createConnector(DisableFederationFeature.INSTANCE);
        Set<ObjectName> names = connector.queryNames(null, null);
        assertTrue(names.contains(new ObjectName("java.lang:type=Runtime")));
        assertTrue(names.contains(new ObjectName("JMImplementation:type=MBeanServerDelegate")));
        boolean serverMBeanSeen = false;
        for (ObjectName name : names) {
            if ("Server".equals(name.getKeyProperty("type"))) {
                if (serverMBeanSeen) {
                    fail();
                } else {
                    assertNull(name.getKeyProperty("cell"));
                    assertNull(name.getKeyProperty("node"));
                    assertNull(name.getKeyProperty("process"));
                    serverMBeanSeen = true;
                }
            }
        }
        assertTrue(serverMBeanSeen);
    }
}
