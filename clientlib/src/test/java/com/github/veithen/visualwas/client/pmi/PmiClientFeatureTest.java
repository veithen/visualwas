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
package com.github.veithen.visualwas.client.pmi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.veithen.visualwas.connector.Connector;
import com.github.veithen.visualwas.connector.transport.dummy.DictionaryRequestMatcher;
import com.github.veithen.visualwas.connector.transport.dummy.DummyTransport;

public class PmiClientFeatureTest {
    private Connector connector;
    private Perf perf;

    @BeforeEach
    public void setUp() throws Exception {
        DummyTransport transport = new DummyTransport(new DictionaryRequestMatcher());
        transport.addExchanges(
                PmiClientFeatureTest.class,
                "getServerMBean",
                "queryNames",
                "invoke-getStatsArray",
                "invoke-getStatsArray2",
                "invoke-getConfigs",
                "invoke-getInstrumentationLevel",
                "invoke-setInstrumentationLevel");
        connector = transport.createConnector(PmiClientFeature.INSTANCE);
        perf = connector.getAdapter(Perf.class);
    }

    @Test
    public void testGetStatsArray() throws Exception {
        Stats[] statsArray =
                perf.getStatsArray(
                        new StatDescriptor[] {
                            new StatDescriptor(PmiModules.THREAD_POOL, "WebContainer")
                        },
                        false);
        assertThat(statsArray).hasSize(1);
        Stats stats = statsArray[0];
        assertThat(stats.getStatsType()).isEqualTo(PmiModules.THREAD_POOL);
        assertThat(stats.getName()).isEqualTo("WebContainer");

        Statistic stat = stats.getStatistic(1);
        assertThat(stat).isInstanceOf(CountStatistic.class);
        assertThat(((CountStatistic) stat).getCount()).isEqualTo(6);

        stat = stats.getStatistic(4);
        assertThat(stat).isInstanceOf(BoundedRangeStatistic.class);
        assertThat(((BoundedRangeStatistic) stat).getCurrent()).isEqualTo(5);
    }

    @Test
    public void testGetStatsArray2() throws Exception {
        Stats[] statsArray =
                perf.getStatsArray(
                        new StatDescriptor[] {
                            new StatDescriptor(
                                    PmiModules.WEB_APP,
                                    "isclite#isclite.war",
                                    PmiModules.WEB_APP_SERVLETS)
                        },
                        true);
        assertThat(statsArray).hasSize(1);
        Stats stats = statsArray[0];
        assertThat(stats.getStatsType()).isEqualTo("com.ibm.ws.wswebcontainer.stats.servletStats");
        assertThat(stats.getName()).isEqualTo(PmiModules.WEB_APP_SERVLETS);
        Stats actionStats = stats.getSubStats("action");
        assertThat(actionStats).isNotNull();
        Statistic stat = actionStats.getStatistic(13);
        assertThat(stat).isInstanceOf(TimeStatistic.class);
        TimeStatistic timeStat = (TimeStatistic) stat;
        assertThat(timeStat.getStartTime()).isEqualTo(1392644768352L);
        assertThat(timeStat.getLastSampleTime()).isEqualTo(1392644905141L);
        assertThat(timeStat.getCount()).isEqualTo(58);
        assertThat(timeStat.getTotal()).isEqualTo(8412);
        assertThat(timeStat.getMin()).isEqualTo(1);
        assertThat(timeStat.getMax()).isEqualTo(1607);
        assertThat(timeStat.getSumOfSquares()).isEqualTo(4176605.0, offset(0.01d));
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
        assertThat(threadPoolConfig).isNotNull();
        assertThat(threadPoolConfig.getDataId("PoolSize")).isEqualTo(4);
        // TODO: validate return value
    }

    @Test
    public void testGetInstrumentationLevel() throws Exception {
        StatLevelSpec[] levels =
                perf.getInstrumentationLevel(
                        new StatDescriptor(PmiModules.THREAD_POOL, "WebContainer"), false);
        assertThat(levels).hasSize(1);
        StatLevelSpec level = levels[0];
        assertThat(level.isEnabled(1)).isTrue();
        assertThat(level.isEnabled(10)).isFalse();
    }

    @Test
    public void testSetInstrumentationLevel() throws Exception {
        StatLevelSpec spec = new StatLevelSpec(PmiModules.THREAD_POOL, "WebContainer");
        spec.enable(1);
        spec.enable(2);
        spec.enable(3);
        perf.setInstrumentationLevel(new StatLevelSpec[] {spec}, Boolean.FALSE);
    }
}
