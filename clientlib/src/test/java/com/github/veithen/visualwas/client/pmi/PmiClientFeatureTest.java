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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.veithen.visualwas.connector.Connector;
import com.github.veithen.visualwas.connector.transport.dummy.DictionaryRequestMatcher;
import com.github.veithen.visualwas.connector.transport.dummy.DummyTransport;

public class PmiClientFeatureTest {
    private Connector connector;
    private Perf perf;
    
    @Before
    public void setUp() throws Exception {
        DummyTransport transport = new DummyTransport(new DictionaryRequestMatcher());
        transport.addExchanges(PmiClientFeatureTest.class, "getServerMBean", "queryNames", "invoke-getStatsObject", "invoke-getConfigs", "invoke-getInstrumentationLevel");
        connector = transport.createConnector(PmiClientFeature.INSTANCE);
        perf = connector.getAdapter(Perf.class);
    }
    
    @Test
    public void testGetStatObject() throws Exception {
        Stats stats = perf.getStatsObject(new MBeanStatDescriptor(connector.getServerMBean(), "threadPoolModule", "WebContainer"), false);
        assertEquals(PmiModules.THREAD_POOL, stats.getStatsType());
        // TODO: validate content
    }
    
    @Test
    public void testGetConfigs() throws Exception {
        perf.getConfigs();
        // TODO: validate return value
    }
    
    @Test
    public void testGetInstrumentationLevel() throws Exception {
        MBeanLevelSpec[] levels = perf.getInstrumentationLevel(new MBeanStatDescriptor(connector.getServerMBean(), "threadPoolModule", "WebContainer"), false);
        assertEquals(1, levels.length);
        MBeanLevelSpec level = levels[0];
        assertTrue(level.isEnabled(1));
        assertFalse(level.isEnabled(10));
    }
}
