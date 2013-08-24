package com.github.veithen.visualwas.connector;

import com.github.veithen.visualwas.connector.transport.Endpoint;

public abstract class ConnectorFactory {
    private static ConnectorFactory instance;
    
    public synchronized static ConnectorFactory getInstance() {
        if (instance == null) {
            try {
                instance = (ConnectorFactory)Class.forName("com.github.veithen.visualwas.connector.impl.ConnectorFactoryImpl").newInstance();
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new Error("Failed to create connector factory");
            }
        }
        return instance;
    }
    
    public abstract Connector createConnector(Endpoint endpoint, ConnectorConfiguration config, Attributes attributes);
}
