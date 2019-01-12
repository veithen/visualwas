/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2019 Andreas Veithen
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

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;

import com.github.veithen.visualwas.connector.feature.Dependencies;
import com.github.veithen.visualwas.connector.feature.Feature;

// TODO: should not be public
public final class DependencyUtil {
    private DependencyUtil() {}

    public static void process(Feature feature, Collection<Feature> unprocessedFeatures, Collection<Feature> processedFeatures) {
        Dependencies ann = feature.getClass().getAnnotation(Dependencies.class);
        if (ann != null) {
            resolveDependencies(ann, unprocessedFeatures, processedFeatures);
        }
        processedFeatures.add(feature);
    }
    
    public static void resolveDependencies(Dependencies ann, Collection<Feature> unprocessedFeatures, Collection<Feature> processedFeatures) {
        deploop: for (Class<? extends Feature> dependencyClass : ann.value()) {
            for (Feature processedFeature : processedFeatures) {
                if (dependencyClass.isInstance(processedFeature)) {
                    continue deploop;
                }
            }
            if (unprocessedFeatures != null) {
                for (Iterator<Feature> it = unprocessedFeatures.iterator(); it.hasNext(); ) {
                    Feature unprocessedFeature = it.next();
                    if (dependencyClass.isInstance(unprocessedFeature)) {
                        it.remove();
                        process(unprocessedFeature, unprocessedFeatures, processedFeatures);
                        continue deploop;
                    }
                }
            }
            Feature dependency;
            try {
                Field instanceField = dependencyClass.getField("INSTANCE");
                // TODO: check that the field is public static final
                Object obj = instanceField.get(null);
                dependency = dependencyClass.isInstance(obj) ? (Feature)obj : null;
            } catch (NoSuchFieldException ex) {
                dependency = null;
            } catch (IllegalAccessException ex) {
                dependency = null;
            }
            if (dependency == null) {
                // TODO: exception type
                throw new RuntimeException("Unsatisfied dependency of type " + dependencyClass.getName());
            }
            process(dependency, unprocessedFeatures, processedFeatures);
        }
    }
}
