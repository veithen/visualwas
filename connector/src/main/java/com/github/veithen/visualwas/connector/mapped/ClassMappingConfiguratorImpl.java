package com.github.veithen.visualwas.connector.mapped;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

final class ClassMappingConfiguratorImpl implements ClassMappingConfigurator {
    private final ClassMapper classMapper;
    private final Set<Class<?>> processedClasses = new HashSet<Class<?>>();

    ClassMappingConfiguratorImpl(ClassMapper classMapper) {
        this.classMapper = classMapper;
    }
    
    @Override
    public void addMappedClasses(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            scan(clazz, true);
        }
    }

    private void scan(Class<?> clazz, boolean requireAnnotation) {
        if (!processedClasses.add(clazz)) {
            return;
        }
        MappedClass ann = clazz.getAnnotation(MappedClass.class);
        if (ann == null) {
            if (requireAnnotation) {
                throw new IllegalArgumentException(clazz.getName() + " doesn't have an annotation of type " + MappedClass.class.getName());
            } else {
                return;
            }
        }
        classMapper.addMapping(ann.value(), clazz.getName());
        scan(clazz.getSuperclass(), false);
        for (Field field : clazz.getDeclaredFields()) {
            scan(field.getType(), false);
        }
    }
}
