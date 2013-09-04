package com.github.veithen.visualwas.connector.notification;

import java.io.Serializable;

import com.github.veithen.visualwas.connector.mapped.MappedClass;

@MappedClass("com.ibm.ws.management.event.ListenerIdentifier")
public final class SubscriptionHandle implements Serializable {
    private static final long serialVersionUID = 7076969905409989521L;
    private final long id;
    
    // SubscriptionHandle instances are only created by deserialization; hide the constructor
    private SubscriptionHandle(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
