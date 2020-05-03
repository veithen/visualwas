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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.modelmbean.RequiredModelMBean;

import org.junit.Test;

import com.github.veithen.visualwas.connector.transport.dummy.DummyTransport;
import com.github.veithen.visualwas.connector.transport.dummy.SequencedRequestMatcher;

public class ConnectorTest {
    @Test
    public void testGetMBeanCount() throws Exception {
        DummyTransport transport = new DummyTransport(new SequencedRequestMatcher());
        transport.addExchange(ConnectorTest.class, "getMBeanCount");
        Connector connector = transport.createConnector();
        assertEquals(Integer.valueOf(146), connector.getMBeanCount());
    }
    
    @Test
    public void testQueryNames() throws Exception {
        DummyTransport transport = new DummyTransport(new SequencedRequestMatcher());
        transport.addExchange(ConnectorTest.class, "queryNames");
        Connector connector = transport.createConnector();
        Iterator<ObjectName> names = connector.queryNames(new ObjectName("WebSphere:type=Server,*"), null).iterator();
        assertTrue(names.hasNext());
        ObjectName name = names.next();
        assertEquals("WebSphere", name.getDomain());
        assertEquals("server1", name.getKeyProperty("name"));
        assertFalse(names.hasNext());
    }
    
    @Test
    public void testQueryMBeans() throws Exception {
        DummyTransport transport = new DummyTransport(new SequencedRequestMatcher());
        transport.addExchange(ConnectorTest.class, "queryMBeans");
        Connector connector = transport.createConnector();
        Iterator<ObjectInstance> mbeans = connector.queryMBeans(new ObjectName("WebSphere:type=Server,*"), null).iterator();
        assertTrue(mbeans.hasNext());
        ObjectInstance mbean = mbeans.next();
        assertEquals(RequiredModelMBean.class.getName(), mbean.getClassName());
        ObjectName name = mbean.getObjectName();
        assertEquals("WebSphere", name.getDomain());
        assertEquals("server1", name.getKeyProperty("name"));
        assertFalse(mbeans.hasNext());
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
        assertNotNull(ret);
    }
}
