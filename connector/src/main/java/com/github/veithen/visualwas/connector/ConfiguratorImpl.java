package com.github.veithen.visualwas.connector;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.veithen.visualwas.connector.feature.AdapterFactory;
import com.github.veithen.visualwas.connector.feature.AlternateClass;
import com.github.veithen.visualwas.connector.feature.Configurator;

final class ConfiguratorImpl implements Configurator {
    private final Set<Class<?>> processedClasses = new HashSet<Class<?>>();
    private final Map<Class<?>,Object> adapters = new HashMap<Class<?>,Object>();
    private List<Interceptor> interceptors;
    private ClassMapper classMapper;
    private AdaptableDelegate adaptableDelegate;

    ConfiguratorImpl(List<Interceptor> interceptors, ClassMapper classMapper, AdaptableDelegate adaptableDelegate) {
        this.interceptors = interceptors;
        this.classMapper = classMapper;
        this.adaptableDelegate = adaptableDelegate;
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
    
    @Override
    public <T> void registerConfiguratorAdapter(Class<T> iface, T adapter) {
        adapters.put(iface, adapter);
    }

    @Override
    public <T> T getAdapter(Class<T> clazz) {
        return clazz.cast(adapters.get(clazz));
    }

    @Override
    public <T> void registerAdminServiceAdapter(Class<T> iface, AdapterFactory<T> adapterFactory) {
        adaptableDelegate.registerAdapter(iface, adapterFactory);
    }

    void release() {
        interceptors = null;
        classMapper = null;
        adaptableDelegate = null;
    }
}
