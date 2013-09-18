package com.github.veithen.visualwas.connector.notification;

import java.io.IOException;

import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;

public interface NotificationDispatcher {
    // TODO: documentation: doesn't check for the existence of the MBean; can use patterns
    void addNotificationListener(ObjectName name, NotificationListener listener, NotificationFilter filter, Object handback) throws IOException;
}
