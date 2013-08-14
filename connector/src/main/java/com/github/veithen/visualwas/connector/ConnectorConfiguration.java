package com.github.veithen.visualwas.connector;

import java.util.ArrayList;
import java.util.Arrays;
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
        private List<Feature> features = new ArrayList<>();
        
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
        
        public ConnectorConfiguration build() {
            ClassMapper classMapper = new ClassMapper();
            ConnectorConfiguratorImpl configurator = new ConnectorConfiguratorImpl(classMapper);
            for (Feature feature : features) {
                feature.configureConnector(configurator);
            }
            configurator.release();
            return new ConnectorConfiguration(
                    transportFactory == null ? TransportFactory.DEFAULT : transportFactory,
                    transportConfiguration == null ? TransportConfiguration.DEFAULT : transportConfiguration,
                    classMapper,
                    classLoaderProvider == null ? ClassLoaderProvider.TCCL : classLoaderProvider,
                    configurator.getInterceptors());
        }
    }
    
    private final TransportFactory transportFactory;
    private final TransportConfiguration transportConfiguration;
    private final ClassMapper classMapper;
    private final ClassLoaderProvider classLoaderProvider;
    private final Interceptor[] interceptors;
    
    ConnectorConfiguration(TransportFactory transportFactory, TransportConfiguration transportConfiguration,
            ClassMapper classMapper, ClassLoaderProvider classLoaderProvider, Interceptor[] interceptors) {
        this.transportFactory = transportFactory;
        this.transportConfiguration = transportConfiguration;
        this.classMapper = classMapper;
        this.classLoaderProvider = classLoaderProvider;
        this.interceptors = interceptors;
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

    ClassMapper getClassMapper() {
        return classMapper;
    }

    public ClassLoaderProvider getClassLoaderProvider() {
        return classLoaderProvider;
    }

    Interceptor[] getInterceptors() {
        return interceptors;
    }
}
