/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2014 Andreas Veithen
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
