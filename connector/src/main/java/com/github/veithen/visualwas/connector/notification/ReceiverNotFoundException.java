package com.github.veithen.visualwas.connector.notification;

import com.github.veithen.visualwas.connector.mapped.MappedClass;

@MappedClass("com.ibm.ws.management.exception.ReceiverNotFoundException")
public class ReceiverNotFoundException extends Exception {
    private static final long serialVersionUID = 836353044717542023L;
    private final ListenerIdentifier id;
    
    public ReceiverNotFoundException(ListenerIdentifier id) {
        this.id = id;
    }
    
    public ListenerIdentifier getListenerIdentifier() {
        return id;
    }
}
