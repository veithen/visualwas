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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
        transport.addExchanges(PmiClientFeatureTest.class, "getServerMBean", "queryNames", "invoke-getStatsArray", "invoke-getStatsArray2", "invoke-getConfigs", "invoke-getInstrumentationLevel", "invoke-setInstrumentationLevel");
        connector = transport.createConnector(PmiClientFeature.INSTANCE);
        perf = connector.getAdapter(Perf.class);
    }
    
    @Test
    public void testGetStatsArray() throws Exception {
        Stats[] statsArray = perf.getStatsArray(new StatDescriptor[] { new StatDescriptor(PmiModules.THREAD_POOL, "WebContainer") }, false);
        assertEquals(1, statsArray.length);
        Stats stats = statsArray[0];
        assertEquals(PmiModules.THREAD_POOL, stats.getStatsType());
        assertEquals("WebContainer", stats.getName());
        
        Statistic stat = stats.getStatistic(1);
        assertTrue(stat instanceof CountStatistic);
        assertEquals(6, ((CountStatistic)stat).getCount());
        
        stat = stats.getStatistic(4);
        assertTrue(stat instanceof BoundedRangeStatistic);
        assertEquals(5, ((BoundedRangeStatistic)stat).getCurrent());
    }
    
    @Test
    public void testGetStatsArray2() throws Exception {
        Stats[] statsArray = perf.getStatsArray(new StatDescriptor[] { new StatDescriptor(PmiModules.WEB_APP, "isclite#isclite.war", PmiModules.WEB_APP_SERVLETS) }, true);
        assertEquals(1, statsArray.length);
        Stats stats = statsArray[0];
        assertEquals("com.ibm.ws.wswebcontainer.stats.servletStats", stats.getStatsType());
        assertEquals(PmiModules.WEB_APP_SERVLETS, stats.getName());
        Stats actionStats = stats.getSubStats("action");
        assertNotNull(actionStats);
        Statistic stat = actionStats.getStatistic(13);
        assertTrue(stat instanceof TimeStatistic);
        TimeStatistic timeStat = (TimeStatistic)stat;
        assertEquals(1392644768352L, timeStat.getStartTime());
        assertEquals(1392644905141L, timeStat.getLastSampleTime());
        assertEquals(58, timeStat.getCount());
        assertEquals(8412, timeStat.getTotal());
        assertEquals(1, timeStat.getMin());
        assertEquals(1607, timeStat.getMax());
        assertEquals(4176605.0, timeStat.getSumOfSquares(), 0.01d);
    }
    
    @Test
    public void testGetConfigs() throws Exception {
        PmiModuleConfig[] configs = perf.getConfigs();
        PmiModuleConfig threadPoolConfig = null;
        for (PmiModuleConfig config : configs) {
            if (config.getUID().equals(PmiModules.THREAD_POOL)) {
                threadPoolConfig = config;
                break;
            }
        }
        assertNotNull(threadPoolConfig);
        assertEquals(4, threadPoolConfig.getDataId("PoolSize"));
        // TODO: validate return value
    }
    
    @Test
    public void testGetInstrumentationLevel() throws Exception {
        StatLevelSpec[] levels = perf.getInstrumentationLevel(new StatDescriptor(PmiModules.THREAD_POOL, "WebContainer"), false);
        assertEquals(1, levels.length);
        StatLevelSpec level = levels[0];
        assertTrue(level.isEnabled(1));
        assertFalse(level.isEnabled(10));
    }
    
    @Test
    public void testSetInstrumentationLevel() throws Exception {
        StatLevelSpec spec = new StatLevelSpec(PmiModules.THREAD_POOL, "WebContainer");
        spec.enable(1);
        spec.enable(2);
        spec.enable(3);
        perf.setInstrumentationLevel(new StatLevelSpec[] { spec }, Boolean.FALSE);
    }
}
