package com.github.veithen.visualwas.connector;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.veithen.visualwas.connector.feature.AlternateClass;
import com.github.veithen.visualwas.connector.feature.ConnectorConfigurator;

final class ConnectorConfiguratorImpl implements ConnectorConfigurator {
    private final Set<Class<?>> processedClasses = new HashSet<Class<?>>();
    private final List<Interceptor> interceptors = new ArrayList<Interceptor>();
    private ClassMapper classMapper;

    ConnectorConfiguratorImpl(ClassMapper classMapper) {
        this.classMapper = classMapper;
    }

    @Override
    public void addInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
    }

    @Override
    public void addAlternateClasses(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            scan(clazz, true);
        }
    }

    private void scan(Class<?> clazz, boolean requireAnnotation) {
        if (!processedClasses.add(clazz)) {
            return;
        }
        AlternateClass ann = clazz.getAnnotation(AlternateClass.class);
        if (ann == null) {
            if (requireAnnotation) {
                throw new IllegalArgumentException(clazz.getName() + " doesn't have an annotation of type " + AlternateClass.class.getName());
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
    
    Interceptor[] getInterceptors() {
        return interceptors.toArray(new Interceptor[interceptors.size()]);
    }
    
    void release() {
        classMapper = null;
    }
}
