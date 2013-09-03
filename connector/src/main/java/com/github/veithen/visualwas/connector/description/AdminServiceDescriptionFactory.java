package com.github.veithen.visualwas.connector.description;


public abstract class AdminServiceDescriptionFactory {
    private static AdminServiceDescriptionFactory instance;
    
    public synchronized static AdminServiceDescriptionFactory getInstance() {
        if (instance == null) {
            try {
                instance = (AdminServiceDescriptionFactory)Class.forName("com.github.veithen.visualwas.connector.impl.AdminServiceDescriptionFactoryImpl").newInstance();
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new Error("Failed to create connector factory");
            }
        }
        return instance;
    }
    public abstract AdminServiceDescription createDescription(Class<?> iface) throws AdminServiceDescriptionFactoryException;
}
