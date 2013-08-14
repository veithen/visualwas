package com.github.veithen.visualwas.connector.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class AnnotationBasedClassMapper implements ClassMapper {
    private final Map<String,String> replacementClasses = new HashMap<String,String>();
    private final Map<String,String> originalClasses = new HashMap<String,String>();
    
    public AnnotationBasedClassMapper(ClassLoader classLoader, String pkg) {
        String index = pkg.replace('.', '/') + "/altclasses.index";
        try {
            InputStream in = classLoader.getResourceAsStream(index);
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                Set<Class<?>> processedClasses = new HashSet<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    String className = pkg + "." + line;
                    try {
                        scan(processedClasses, classLoader.loadClass(className));
                    } catch (ClassNotFoundException ex) {
                        throw new NoClassDefFoundError(ex.getMessage());
                    }
                }
            } finally {
                in.close();
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read " + index, ex);
        }
    }
    
    private void scan(Set<Class<?>> processedClasses, Class<?> clazz) {
        if (!processedClasses.add(clazz)) {
            return;
        }
        AlternateClass ann = clazz.getAnnotation(AlternateClass.class);
        if (ann == null) {
            return;
        }
        replacementClasses.put(ann.value(), clazz.getName());
        originalClasses.put(clazz.getName(), ann.value());
        scan(processedClasses, clazz.getSuperclass());
        for (Field field : clazz.getDeclaredFields()) {
            scan(processedClasses, field.getType());
        }
    }
    
    @Override
    public String getReplacementClass(String originalClass) {
        return replacementClasses.get(originalClass);
    }

    @Override
    public String getOriginalClass(String replacementClass) {
        return originalClasses.get(replacementClass);
    }
}
