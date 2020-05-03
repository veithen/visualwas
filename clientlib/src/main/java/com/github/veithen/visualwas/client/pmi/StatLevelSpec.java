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

@MappedClass("com.ibm.websphere.pmi.stat.StatLevelSpec")
public final class StatLevelSpec implements Serializable {
    private static final long serialVersionUID = -5784101041304135137L;

    private String[] path;
    private int[] enable = new int[0];
    
    public StatLevelSpec(String... path) {
        this.path = path;
    }
    
    public String[] getPath() {
        return path;
    }

    private int getIndex(int dataId) {
        for (int i=0; i<enable.length; i++) {
            if (enable[i] == dataId) {
                return i;
            }
        }
        return -1;
    }
    
    public boolean isEnabled(int dataId) {
        return getIndex(dataId) != -1;
    }
    
    public void enable(int dataId) {
        if (getIndex(dataId) == -1) {
            int[] newEnable = new int[enable.length+1];
            System.arraycopy(enable, 0, newEnable, 0, enable.length);
            newEnable[enable.length] = dataId;
            enable = newEnable;
        }
    }
    
    public void disable(int dataId) {
        int idx = getIndex(dataId);
        if (idx != -1) {
            int[] newEnable = new int[enable.length-1];
            System.arraycopy(enable, 0, newEnable, 0, idx);
            System.arraycopy(enable, idx+1, newEnable, idx, enable.length-1-idx);
            enable = newEnable;
        }
    }
}
