package com.github.veithen.visualwas.connector.notification;

import java.io.IOException;

import javax.management.MBeanServerConnection;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;

public interface NotificationDispatcher {
    /**
     * Add a listener for notifications matching a given filter and emitted by MBeans matching a
     * given pattern. In contrast to
     * {@link MBeanServerConnection#addNotificationListener(ObjectName, NotificationListener, NotificationFilter, Object)},
     * the listener may receive notifications from zero or more MBeans, including MBeans
     * registered in the future.
     * 
     * @param name
     *            a pattern matching zero or more MBeans
     * @param listener
     *            the listener object which will handle the notifications emitted by the MBeans
     * @param handback
     *            the context to be sent to the listener when a notification is emitted
     */
    void addNotificationListener(ObjectName name, NotificationListener listener, NotificationFilter filter, Object handback) throws IOException;
}
