package com.github.veithen.visualwas.connector.mapped;

import java.util.HashMap;
import java.util.Map;

final class ClassMapper {
    private final Map<String,String> replacementClasses = new HashMap<String,String>();
    private final Map<String,String> originalClasses = new HashMap<String,String>();
    
    void addMapping(String originalClass, String replacementClass) {
        replacementClasses.put(originalClass, replacementClass);
        originalClasses.put(replacementClass, originalClass);
    }
    
    String getReplacementClass(String originalClass) {
        return replacementClasses.get(originalClass);
    }

    String getOriginalClass(String replacementClass) {
        return originalClasses.get(replacementClass);
    }
}
