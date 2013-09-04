package com.github.veithen.visualwas.connector.notification;

import com.github.veithen.visualwas.connector.mapped.MappedClass;

@MappedClass("com.ibm.ws.management.exception.ReceiverNotFoundException")
public class SubscriptionNotFoundException extends Exception {
    private static final long serialVersionUID = 836353044717542023L;
    private final SubscriptionHandle id;
    
    public SubscriptionNotFoundException(SubscriptionHandle id) {
        this.id = id;
    }
    
    public SubscriptionHandle getListenerIdentifier() {
        return id;
    }
}
