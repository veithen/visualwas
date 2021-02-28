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
package com.github.veithen.visualwas.connector;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Iterator;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.modelmbean.RequiredModelMBean;

import org.junit.jupiter.api.Test;

import com.github.veithen.visualwas.connector.transport.dummy.DummyTransport;
import com.github.veithen.visualwas.connector.transport.dummy.SequencedRequestMatcher;

public class ConnectorTest {
    @Test
    public void testGetMBeanCount() throws Exception {
        DummyTransport transport = new DummyTransport(new SequencedRequestMatcher());
        transport.addExchange(ConnectorTest.class, "getMBeanCount");
        Connector connector = transport.createConnector();
        assertThat(connector.getMBeanCount()).isEqualTo(146);
    }

    @Test
    public void testQueryNames() throws Exception {
        DummyTransport transport = new DummyTransport(new SequencedRequestMatcher());
        transport.addExchange(ConnectorTest.class, "queryNames");
        Connector connector = transport.createConnector();
        Iterator<ObjectName> names =
                connector.queryNames(new ObjectName("WebSphere:type=Server,*"), null).iterator();
        assertThat(names.hasNext()).isTrue();
        ObjectName name = names.next();
        assertThat(name.getDomain()).isEqualTo("WebSphere");
        assertThat(name.getKeyProperty("name")).isEqualTo("server1");
        assertThat(names.hasNext()).isFalse();
    }

    @Test
    public void testQueryMBeans() throws Exception {
        DummyTransport transport = new DummyTransport(new SequencedRequestMatcher());
        transport.addExchange(ConnectorTest.class, "queryMBeans");
        Connector connector = transport.createConnector();
        Iterator<ObjectInstance> mbeans =
                connector.queryMBeans(new ObjectName("WebSphere:type=Server,*"), null).iterator();
        assertThat(mbeans.hasNext()).isTrue();
        ObjectInstance mbean = mbeans.next();
        assertThat(mbean.getClassName()).isEqualTo(RequiredModelMBean.class.getName());
        ObjectName name = mbean.getObjectName();
        assertThat(name.getDomain()).isEqualTo("WebSphere");
        assertThat(name.getKeyProperty("name")).isEqualTo("server1");
        assertThat(mbeans.hasNext()).isFalse();
    }

    @Test
    public void testSetAttributes() throws Exception {
        DummyTransport transport = new DummyTransport(new SequencedRequestMatcher());
        transport.addExchanges(ConnectorTest.class, "getServerMBean", "setAttributes");
        Connector connector = transport.createConnector();
        ObjectName server = connector.getServerMBean();
        AttributeList attributes = new AttributeList();
        attributes.add(new Attribute("threadMonitorAdjustmentThreshold", Integer.valueOf(100)));
        attributes.add(new Attribute("threadMonitorInterval", Integer.valueOf(180)));
        attributes.add(new Attribute("threadMonitorThreshold", Integer.valueOf(600)));
        AttributeList ret = connector.setAttributes(server, attributes);
        assertThat(ret).isNotNull();
    }
}
