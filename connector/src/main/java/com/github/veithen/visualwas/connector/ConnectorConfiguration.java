package com.github.veithen.visualwas.connector;

import com.github.veithen.visualwas.connector.loader.ClassLoaderProvider;
import com.github.veithen.visualwas.connector.loader.ClassMapper;
import com.github.veithen.visualwas.connector.transport.TransportConfiguration;
import com.github.veithen.visualwas.connector.transport.TransportFactory;

public final class ConnectorConfiguration {
    public static final class Builder {
        private TransportFactory transportFactory;
        private TransportConfiguration transportConfiguration;
        private ClassMapper classMapper;
        private ClassLoaderProvider classLoaderProvider;
        
        public Builder setTransportFactory(TransportFactory transportFactory) {
            this.transportFactory = transportFactory;
            return this;
        }
        
        public Builder setTransportConfiguration(TransportConfiguration transportConfiguration) {
            this.transportConfiguration = transportConfiguration;
            return this;
        }
        
        public Builder setClassMapper(ClassMapper classMapper) {
            this.classMapper = classMapper;
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
        
        public ConnectorConfiguration build() {
            return new ConnectorConfiguration(
                    transportFactory == null ? TransportFactory.DEFAULT : transportFactory,
                    transportConfiguration == null ? TransportConfiguration.DEFAULT : transportConfiguration,
                    classMapper == null ? ClassMapper.NULL : classMapper,
                    classLoaderProvider == null ? ClassLoaderProvider.TCCL : classLoaderProvider);
        }
    }
    
    private final TransportFactory transportFactory;
    private final TransportConfiguration transportConfiguration;
    private final ClassMapper classMapper;
    private final ClassLoaderProvider classLoaderProvider;
    
    ConnectorConfiguration(TransportFactory transportFactory, TransportConfiguration transportConfiguration,
            ClassMapper classMapper, ClassLoaderProvider classLoaderProvider) {
        this.transportFactory = transportFactory;
        this.transportConfiguration = transportConfiguration;
        this.classMapper = classMapper;
        this.classLoaderProvider = classLoaderProvider;
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

    public ClassMapper getClassMapper() {
        return classMapper;
    }

    public ClassLoaderProvider getClassLoaderProvider() {
        return classLoaderProvider;
    }
}
