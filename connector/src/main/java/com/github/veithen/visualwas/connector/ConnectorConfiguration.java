package com.github.veithen.visualwas.connector;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.github.veithen.visualwas.connector.feature.Dependencies;
import com.github.veithen.visualwas.connector.feature.Feature;
import com.github.veithen.visualwas.connector.loader.ClassLoaderProvider;
import com.github.veithen.visualwas.connector.transport.TransportConfiguration;
import com.github.veithen.visualwas.connector.transport.TransportFactory;

public final class ConnectorConfiguration {
    public static final class Builder {
        private TransportFactory transportFactory;
        private TransportConfiguration transportConfiguration;
        private ClassLoaderProvider classLoaderProvider;
        private List<Feature> features = new ArrayList<Feature>();
        
        public Builder setTransportFactory(TransportFactory transportFactory) {
            this.transportFactory = transportFactory;
            return this;
        }
        
        public Builder setTransportConfiguration(TransportConfiguration transportConfiguration) {
            this.transportConfiguration = transportConfiguration;
            return this;
        }
        
        /**
         * Set the class loader provider. If no provider is set explicitly,
         * {@link ClassLoaderProvider#TCCL} will be used.
         * 
         * @param classLoaderProvider
         *            the class loader provider
         * @return this builder
         */
        public Builder setClassLoaderProvider(ClassLoaderProvider classLoaderProvider) {
            this.classLoaderProvider = classLoaderProvider;
            return this;
        }
        
        public Builder addFeatures(Feature... features) {
            this.features.addAll(Arrays.asList(features));
            return this;
        }
        
        private static void process(Feature feature, Collection<Feature> unprocessedFeatures, Collection<Feature> processedFeatures) {
            Dependencies ann = feature.getClass().getAnnotation(Dependencies.class);
            if (ann != null) {
                deploop: for (Class<? extends Feature> dependencyClass : ann.value()) {
                    for (Feature processedFeature : processedFeatures) {
                        if (dependencyClass.isInstance(processedFeature)) {
                            continue deploop;
                        }
                    }
                    for (Iterator<Feature> it = unprocessedFeatures.iterator(); it.hasNext(); ) {
                        Feature unprocessedFeature = it.next();
                        if (dependencyClass.isInstance(unprocessedFeature)) {
                            it.remove();
                            process(feature, unprocessedFeatures, processedFeatures);
                            continue deploop;
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
            processedFeatures.add(feature);
        }
        
        // TODO: describe defaults
        // TODO: describe dependency resolution
        public ConnectorConfiguration build() {
            Deque<Feature> unprocessedFeatures = new LinkedList<Feature>(features);
            List<Feature> processedFeatures = new ArrayList<Feature>();
            while (!unprocessedFeatures.isEmpty()) {
                process(unprocessedFeatures.removeFirst(), unprocessedFeatures, processedFeatures);
            }
            return new ConnectorConfiguration(
                    transportFactory == null ? TransportFactory.DEFAULT : transportFactory,
                    transportConfiguration == null ? TransportConfiguration.DEFAULT : transportConfiguration,
                    classLoaderProvider == null ? ClassLoaderProvider.TCCL : classLoaderProvider,
                    processedFeatures.toArray(new Feature[processedFeatures.size()]));
        }
    }
    
    private final TransportFactory transportFactory;
    private final TransportConfiguration transportConfiguration;
    private final ClassLoaderProvider classLoaderProvider;
    private final Feature[] features;
    
    ConnectorConfiguration(TransportFactory transportFactory, TransportConfiguration transportConfiguration,
            ClassLoaderProvider classLoaderProvider, Feature[] features) {
        this.transportFactory = transportFactory;
        this.transportConfiguration = transportConfiguration;
        this.classLoaderProvider = classLoaderProvider;
        this.features = features;
    }

    public static Builder custom() {
        return new Builder();
    }
    
    public TransportFactory getTransportFactory() {
        return transportFactory;
    }

    public TransportConfiguration getTransportConfiguration() {
        return transportConfiguration;
    }

    public ClassLoaderProvider getClassLoaderProvider() {
        return classLoaderProvider;
    }

    Feature[] getFeatures() {
        return features;
    }
}
