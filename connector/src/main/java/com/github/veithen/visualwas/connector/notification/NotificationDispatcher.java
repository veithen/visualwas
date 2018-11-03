/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2018 Andreas Veithen
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package com.github.veithen.visualwas.connector.notification;

import java.io.IOException;

import javax.management.ListenerNotFoundException;
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

    void removeNotificationListener(ObjectName name, NotificationListener listener, NotificationFilter filter, Object handback) throws IOException, ListenerNotFoundException;
}
