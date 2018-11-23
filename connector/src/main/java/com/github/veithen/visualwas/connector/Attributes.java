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
package com.github.veithen.visualwas.connector;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class Attributes {
    public static final class Builder {
        private final Attributes parentAttributes;
        private final Map<Class<?>,Object> map = new HashMap<>();
        
        private Builder(Attributes parentAttributes) {
            this.parentAttributes = parentAttributes;
        }
        
        public <T> Builder set(Class<T> key, T value) {
            map.put(key, value);
            return this;
        }
        
        public Attributes build() {
            return new Attributes(parentAttributes, map);
        }
    }
    
    private final Attributes parentAttributes;
    private final Map<Class<?>,Object> map;
    
    private Attributes(Attributes parentAttributes, Map<Class<?>,Object> map) {
        this.parentAttributes = parentAttributes;
        this.map = map;
    }
    
    public static Builder builder() {
        return new Builder(null);
    }
    
    public static Builder builder(Attributes attributes) {
        return new Builder(attributes);
    }
    
    public <T> T get(Class<T> key) {
        if (map.containsKey(key)) {
            return key.cast(map.get(key));
        } else if (parentAttributes == null) {
            return null;
        } else {
            return parentAttributes.get(key);
        }
    }
    
    public Set<Class<?>> keySet() {
        return Collections.unmodifiableSet(map.keySet());
    }
}
