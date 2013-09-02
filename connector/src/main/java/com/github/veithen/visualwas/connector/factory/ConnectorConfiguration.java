package com.github.veithen.visualwas.connector.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

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
        
        // TODO: describe defaults
        // TODO: describe dependency resolution
        public ConnectorConfiguration build() {
            Deque<Feature> unprocessedFeatures = new LinkedList<Feature>(features);
            List<Feature> processedFeatures = new ArrayList<Feature>();
            while (!unprocessedFeatures.isEmpty()) {
                DependencyUtil.process(unprocessedFeatures.removeFirst(), unprocessedFeatures, processedFeatures);
            }
            return new ConnectorConfiguration(
                    transportFactory == null ? TransportFactory.DEFAULT : transportFactory,
                    transportConfiguration == null ? TransportConfiguration.DEFAULT : transportConfiguration,
                    classLoaderProvider == null ? ClassLoaderProvider.TCCL : classLoaderProvider,
                    processedFeatures);
        }
    }
    
    public static final ConnectorConfiguration DEFAULT = ConnectorConfiguration.custom().build();
    
    private final TransportFactory transportFactory;
    private final TransportConfiguration transportConfiguration;
    private final ClassLoaderProvider classLoaderProvider;
    private final List<Feature> features;
    
    ConnectorConfiguration(TransportFactory transportFactory, TransportConfiguration transportConfiguration,
            ClassLoaderProvider classLoaderProvider, List<Feature> features) {
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

    // TODO: should not be public
    public List<Feature> getFeatures() {
        return features;
    }
}
