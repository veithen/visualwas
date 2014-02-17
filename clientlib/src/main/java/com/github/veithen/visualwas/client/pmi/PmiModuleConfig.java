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
import java.util.HashMap;
import java.util.Map;

import com.github.veithen.visualwas.connector.mapped.MappedClass;

@MappedClass("com.ibm.websphere.pmi.PmiModuleConfig")
public final class PmiModuleConfig implements Serializable {
    private static final long serialVersionUID = 9139791110927568058L;

    private String UID;
    private String description;
    private String mbeanType;
    private HashMap<Integer,PmiDataInfo> perfData;
    private String statsNLSFile;
    private int[] dependList;
    private boolean hasSubMod;
    private boolean hasSubModInit;

    public String getUID() {
        return UID;
    }
    
    public int getDataId(String name) {
        for (Map.Entry<Integer,PmiDataInfo> entry : perfData.entrySet()) {
            if (entry.getValue().getName().equals(name)) {
                return entry.getKey();
            }
        }
        return -1;
    }
    
    public PmiDataInfo[] listAllData() {
        return perfData.values().toArray(new PmiDataInfo[perfData.size()]);
    }
}
