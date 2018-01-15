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
package com.github.veithen.visualwas.client.pmi;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.veithen.visualwas.client.WebSphereITCase;
import com.github.veithen.visualwas.connector.feature.Feature;

public class PmiClientFeatureITCase extends WebSphereITCase {
    @Override
    protected Feature[] getFeatures() {
        return new Feature[] { PmiClientFeature.INSTANCE };
    }

    @Test
    public void testThreadPoolStats() throws Exception {
        Stats[] statsArray = connector.getAdapter(Perf.class).getStatsArray(
                new StatDescriptor[] { new StatDescriptor(PmiModules.THREAD_POOL, "Default") }, false);
        assertEquals(1, statsArray.length);
        assertThat(statsArray[0].toString()).matches(
                "\\s*Stats name=Default, type=threadPoolModule\\s*\\{\\s*\n" +
                "\\s*name=ActiveCount, ID=3, description=The number of concurrently active threads\\., " +
                "unit=N/A, type=BoundedRangeStatistic, lowWaterMark=0, highWaterMark=0, current=0, " +
                "integral=0\\.0, lowerBound=0, upperBound=0\\s*\n" +
                "\\s*name=PoolSize, ID=4, description=The average number of threads in a pool\\., " +
                "unit=N/A, type=BoundedRangeStatistic, lowWaterMark=20, highWaterMark=20, current=0, " +
                "integral=0\\.0, lowerBound=20, upperBound=20\\s*\n" +
                "\\}\\s*");

    }
}
