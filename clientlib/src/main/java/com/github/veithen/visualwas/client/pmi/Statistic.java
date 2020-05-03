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

import java.io.Serializable;

import com.github.veithen.visualwas.connector.mapped.MappedClass;

@MappedClass("com.ibm.ws.pmi.stat.StatisticImpl")
public abstract class Statistic implements Serializable {
    private static final long serialVersionUID = 1358353157061734347L;

    private int id;
    private long startTime;
    private long lastSampleTime;
    private transient PmiDataInfo dataInfo;
    
    public int getId() {
        return id;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getLastSampleTime() {
        return lastSampleTime;
    }

    void setDataInfo(PmiDataInfo dataInfo) {
        this.dataInfo = dataInfo;
    }
    
    public String getName() {
        return dataInfo == null ? null : dataInfo.getName();
    }

    public String getDescription() {
        return dataInfo == null ? null : dataInfo.getDescription();
    }
    
    public String getUnit() {
        return dataInfo == null ? null : dataInfo.getUnit();
    }
    
    @Override
    public final String toString() {
        StringBuilder buffer = new StringBuilder();
        format(buffer);
        return buffer.toString();
    }

    void format(StringBuilder buffer) {
        buffer.append("name=");
        buffer.append(getName());
        buffer.append(", ID=");
        buffer.append(id);
        buffer.append(", description=");
        buffer.append(getDescription());
        buffer.append(", unit=");
        buffer.append(getUnit());
        buffer.append(", type=");
        buffer.append(getClass().getSimpleName());
    }
}
