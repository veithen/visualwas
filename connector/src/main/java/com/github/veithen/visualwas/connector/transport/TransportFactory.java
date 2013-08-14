package com.github.veithen.visualwas.connector.transport;


public interface TransportFactory {
    TransportFactory DEFAULT = new TransportFactory() {
        @Override
        public Transport createTransport(Endpoint endpoint, TransportConfiguration config) {
            return new DefaultTransport(endpoint, config);
        }
    };
    
    Transport createTransport(Endpoint endpoint, TransportConfiguration config);
}
