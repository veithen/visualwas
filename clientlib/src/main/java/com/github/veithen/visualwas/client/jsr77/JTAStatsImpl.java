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

import javax.management.j2ee.statistics.CountStatistic;
import javax.management.j2ee.statistics.JTAStats;

import com.github.veithen.visualwas.connector.mapped.MappedClass;

@MappedClass("com.ibm.ws.pmi.j2ee.JTAStatsImpl")
final class JTAStatsImpl extends StatsImpl implements JTAStats {
    private static final long serialVersionUID = -3454705613246927618L;

    @Override
    public CountStatistic getActiveCount() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public CountStatistic getCommittedCount() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public CountStatistic getRolledbackCount() {
        // TODO
        throw new UnsupportedOperationException();
    }
}
