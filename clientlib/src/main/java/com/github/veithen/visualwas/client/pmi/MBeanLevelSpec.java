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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import com.github.veithen.visualwas.connector.mapped.MappedClass;

@MappedClass("com.ibm.websphere.pmi.stat.MBeanLevelSpec")
public final class MBeanLevelSpec implements Serializable {
    private static final long serialVersionUID = -3519125759099800020L;

    private static final ObjectStreamField[] serialPersistentFields = {
        new ObjectStreamField("enable", int[].class),
    };

    private Set<Integer> enabled;
    
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        GetField fields = stream.readFields();
        enabled = new LinkedHashSet<>();
        for (int i : (int[])fields.get("enable", null)) {
            enabled.add(i);
        }
    }
    
    public boolean isEnabled(int dataId) {
        return enabled.contains(dataId);
    }
    
    public void enabled(int dataId) {
        enabled.add(dataId);
    }
    
    public void disable(int dataId) {
        enabled.remove(dataId);
    }
}
