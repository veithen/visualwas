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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.github.veithen.visualwas.connector.mapped.MappedClass;

@MappedClass("com.ibm.ws.pmi.stat.StatsImpl")
public final class Stats implements Serializable {
    private static final long serialVersionUID = -5812710047173154854L;

    private String statsType;
    private String name;
    private int type;
    private int instrumentationLevel;
    private List<Statistic> dataMembers;
    private ArrayList<Stats> subCollections;
    private long time;
    
    public String getStatsType() {
        return statsType;
    }
    
    public String getName() {
        return name;
    }

    public Statistic getStatistic(int dataId) {
        for (Statistic s : dataMembers) {
            if (s.getId() == dataId) {
                return s;
            }
        }
        return null;
    }
    
    public Stats getSubStats(String name) {
        for (Stats stats : subCollections) {
            if (stats.getName().equals(name)) {
                return stats;
            }
        }
        return null;
    }
}
