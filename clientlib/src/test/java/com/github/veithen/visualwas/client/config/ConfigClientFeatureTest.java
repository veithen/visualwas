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
package com.github.veithen.visualwas.client.config;

import static org.junit.Assert.assertEquals;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.ObjectName;

import org.junit.Test;

import com.github.veithen.visualwas.connector.Connector;
import com.github.veithen.visualwas.connector.mapped.Session;
import com.github.veithen.visualwas.connector.transport.dummy.DictionaryRequestMatcher;
import com.github.veithen.visualwas.connector.transport.dummy.DummyTransport;

public class ConfigClientFeatureTest {
    private static void assertAttribute(AttributeList attrs, int index, String name, Object value) {
        Attribute attr = attrs.asList().get(index);
        assertEquals(name, attr.getName());
        assertEquals(value, attr.getValue());
    }

    @Test
    public void test() throws Exception {
        DummyTransport transport = new DummyTransport(new DictionaryRequestMatcher());
        transport.addExchanges(
                ConfigClientFeatureTest.class,
                "getServerMBean",
                "queryNames",
                "invoke-resolve",
                "invoke-getAttribute",
                "invoke-getAttributes",
                "invoke-discard");
        Connector connector = transport.createConnector(ConfigClientFeature.INSTANCE);
        ConfigService config = connector.getAdapter(ConfigService.class);
        Session session = new Session(1392883269705L, "test", false);
        ObjectName threadPool =
                config.resolve(
                                session,
                                "Server=server1:ThreadPoolManager=:ThreadPool=WebContainer")[0];
        assertEquals(
                Integer.valueOf(60000),
                config.getAttribute(session, threadPool, "inactivityTimeout"));
        AttributeList attrs =
                config.getAttributes(
                        session, threadPool, new String[] {"minimumSize", "maximumSize"}, false);
        assertEquals(5, attrs.size());
        assertAttribute(attrs, 0, "minimumSize", Integer.valueOf(5));
        assertAttribute(attrs, 1, "maximumSize", Integer.valueOf(10));
        // TODO: use constants here
        assertAttribute(
                attrs,
                2,
                "_Websphere_Config_Data_Id",
                new ConfigDataId(
                        "cells/phobosNode03Cell/nodes/phobosNode03/servers/server1",
                        "server.xml#ThreadPool_1183121908657"));
        assertAttribute(attrs, 3, "_Websphere_Config_Data_Type", "ThreadPool");
        assertAttribute(attrs, 4, "_Websphere_Config_Data_Version", null);
        config.discard(session);
    }
}
