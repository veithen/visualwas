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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.github.veithen.visualwas.connector.mapped.MappedClass;
import com.github.veithen.visualwas.connector.mapped.MappedObjectInputStream;

@MappedClass("com.ibm.ws.pmi.stat.StatsImpl")
public class Stats implements Serializable {
    private static final long serialVersionUID = -5812710047173154854L;

    private String statsType;
    private String name;
    private int type;
    private int instrumentationLevel;
    private List<Statistic> dataMembers;
    private ArrayList<Stats> subCollections;
    private long time;
    
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        PmiModuleConfig config = (((MappedObjectInputStream)stream).getInvocationContext()).getAttribute(Configs.class).getConfig(statsType);
        if (config != null) {
            for (Statistic stat : dataMembers) {
                stat.setDataInfo(config.getDataInfo(stat.getId()));
            }
        }
    }

    public final String getStatsType() {
        return statsType;
    }
    
    public final String getName() {
        return name;
    }

    public final Statistic getStatistic(int dataId) {
        for (Statistic s : dataMembers) {
            if (s.getId() == dataId) {
                return s;
            }
        }
        return null;
    }
    
    public final Stats getSubStats(String name) {
        for (Stats stats : subCollections) {
            if (stats.getName().equals(name)) {
                return stats;
            }
        }
        return null;
    }
    
    @Override
    public final String toString() {
        StringBuilder buffer = new StringBuilder();
        format(buffer, 0);
        return buffer.toString();
    }
    
    private static void indent(StringBuilder buffer, int amount) {
        for (int i=0; i<amount; i++) {
            buffer.append("  ");
        }
    }
    
    private final void format(StringBuilder buffer, int indent) {
        indent(buffer, indent);
        buffer.append("Stats name=");
        buffer.append(name);
        buffer.append(", type=");
        buffer.append(statsType);
        buffer.append('\n');
        indent(buffer, indent);
        buffer.append("{\n");
        if (dataMembers != null) {
            for (Statistic stat : dataMembers) {
                indent(buffer, indent+1);
                buffer.append(stat);
                buffer.append('\n');
            }
        }
        if (subCollections != null) {
            for (Stats stats : subCollections) {
                stats.format(buffer, indent+1);
                buffer.append('\n');
            }
        }
        indent(buffer, indent);
        buffer.append("}");
    }
}
