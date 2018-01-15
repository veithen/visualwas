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
package com.github.veithen.visualwas.connector.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class Attributes {
    private final Map<Class<?>,Object> map;
    
    public Attributes() {
        map = new HashMap<Class<?>,Object>();
    }
    
    public Attributes(Attributes atts) {
        map = atts == null ? new HashMap<Class<?>,Object>() : new HashMap<Class<?>,Object>(atts.map);
    }
    
    public <T> T get(Class<T> key) {
        return key.cast(map.get(key));
    }
    
    public <T> void set(Class<T> key, T value) {
        map.put(key, value);
    }
    
    public Set<Class<?>> keySet() {
        return map.keySet();
    }
}
