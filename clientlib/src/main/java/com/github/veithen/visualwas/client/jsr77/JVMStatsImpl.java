/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2019 Andreas Veithen
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
package com.github.veithen.visualwas.client.jsr77;

import javax.management.j2ee.statistics.BoundedRangeStatistic;
import javax.management.j2ee.statistics.CountStatistic;
import javax.management.j2ee.statistics.JVMStats;

import com.github.veithen.visualwas.connector.mapped.MappedClass;

@MappedClass("com.ibm.ws.pmi.j2ee.JVMStatsImpl")
final class JVMStatsImpl extends StatsImpl implements JVMStats {
    private static final long serialVersionUID = 6643035563502328454L;

    @Override
    public CountStatistic getUpTime() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public BoundedRangeStatistic getHeapSize() {
        // TODO
        throw new UnsupportedOperationException();
    }
}
