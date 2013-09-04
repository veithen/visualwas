package com.github.veithen.visualwas.connector.notification;

import java.io.Serializable;

import javax.management.NotificationFilter;
import javax.management.ObjectName;

import com.github.veithen.visualwas.connector.mapped.MappedClass;

@MappedClass("com.ibm.ws.management.event.NotificationInfo")
final class NotificationInfo implements Serializable {
    private static final long serialVersionUID = 6448465470064588827L;
    
    private final ObjectName name;
    private final NotificationFilter filter;
    
    NotificationInfo(ObjectName name, NotificationFilter filter) {
        this.name = name;
        this.filter = filter;
    }

    ObjectName getName() {
        return name;
    }

    NotificationFilter getFilter() {
        return filter;
    }
}
