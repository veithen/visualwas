/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2016 Andreas Veithen
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

import java.io.Serializable;

import javax.management.j2ee.statistics.Statistic;

import com.github.veithen.visualwas.client.pmi.Stats;
import com.github.veithen.visualwas.connector.mapped.MappedClass;

@MappedClass("com.ibm.ws.pmi.j2ee.StatsImpl")
class StatsImpl implements javax.management.j2ee.statistics.Stats, Serializable {
    private static final long serialVersionUID = 5812249877869525897L;

    private Stats wsImpl;

    @Override
    public final Statistic getStatistic(String statisticName) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public final String[] getStatisticNames() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public final Statistic[] getStatistics() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public final String toString() {
        return wsImpl.toString();
    }
}
