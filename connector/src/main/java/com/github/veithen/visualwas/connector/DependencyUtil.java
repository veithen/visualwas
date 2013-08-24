package com.github.veithen.visualwas.connector;

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
