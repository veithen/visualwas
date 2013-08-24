package com.github.veithen.visualwas.connector;

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
