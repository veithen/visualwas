package com.github.veithen.visualwas.connector.notification;

import java.io.Serializable;

import com.github.veithen.visualwas.connector.mapped.MappedClass;

@MappedClass("com.ibm.ws.management.event.ListenerIdentifier")
public class ListenerIdentifier implements Serializable {
    private static final long serialVersionUID = 7076969905409989521L;
    private final long id;
    
    public ListenerIdentifier(long id) {
        this.id = id;
    }
}
