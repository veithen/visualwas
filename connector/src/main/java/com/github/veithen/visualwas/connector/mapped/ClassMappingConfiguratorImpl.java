/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 Andreas Veithen
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
