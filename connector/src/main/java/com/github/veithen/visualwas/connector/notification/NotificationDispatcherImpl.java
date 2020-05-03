/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2020 Andreas Veithen
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;

import javax.management.ListenerNotFoundException;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.veithen.visualwas.connector.feature.CloseListener;

final class NotificationDispatcherImpl implements NotificationDispatcher, CloseListener {
    private static final Log log = LogFactory.getLog(NotificationDispatcherImpl.class);
    
    private static final int MIN_DELAY = 2000;
    private static final int MAX_DELAY = 30000;
    
    private final RemoteNotificationService service;
    private final Executor executor;
    private final List<NotificationListenerRegistration> registrations = new LinkedList<>();
    // We use a single subscription for all listeners (to avoid blocking multiple threads on the server side)
    private SubscriptionInfo subscriptionInfo = new SubscriptionInfo();
    private SubscriptionHandle subscriptionHandle;
    private int delay = MIN_DELAY;

    NotificationDispatcherImpl(RemoteNotificationService service, Executor executor) {
        this.service = service;
        this.executor = executor;
    }

    private void reconcileSubscriptions() throws IOException {
        Set<NotificationSelector> selectors = new HashSet<>();
        for (NotificationListenerRegistration registration : registrations) {
            selectors.add(registration.getSelector());
        }
        if (selectors.equals(subscriptionInfo.getSelectors())) {
            return;
        }
        subscriptionInfo.setSelectors(selectors);
        if (subscriptionHandle == null && !selectors.isEmpty()) {
            subscriptionHandle = service.addSubscription(subscriptionInfo, null);
            executor.execute(this::run);
        } else if (subscriptionHandle != null && selectors.isEmpty()) {
            try {
                service.removeSubscription(subscriptionHandle);
            } catch (SubscriptionNotFoundException ex) {
                // Ignore
            }
            subscriptionHandle = null;
        } else if (subscriptionHandle != null) {
            try {
                service.updateSubscription(subscriptionHandle, subscriptionInfo);
            } catch (SubscriptionNotFoundException ex) {
                subscriptionHandle = service.addSubscription(subscriptionInfo, null);
            }
        }
    }

    @Override
    public synchronized void addNotificationListener(ObjectName name, NotificationListener listener, NotificationFilter filter, Object handback) throws IOException {
        NotificationSelector selector = new NotificationSelector(name, filter);
        registrations.add(new NotificationListenerRegistration(selector, listener, handback));
        reconcileSubscriptions();
    }

    @Override
    public synchronized void removeNotificationListener(ObjectName name, NotificationListener listener, NotificationFilter filter, Object handback) throws IOException, ListenerNotFoundException {
        for (Iterator<NotificationListenerRegistration> it = registrations.iterator(); it.hasNext(); ) {
            NotificationListenerRegistration registration = it.next();
            if (registration.getListener() == listener
                    && registration.getSelector().getName().equals(name)
                    && Objects.equals(registration.getSelector().getFilter(), filter)
                    && Objects.equals(registration.getHandback(), handback)) {
                it.remove();
                reconcileSubscriptions();
                return;
            }
        }
        throw new ListenerNotFoundException();
    }

    private void run() {
        boolean doDelay = false;
        while (true) {
            if (doDelay) {
                delay = Math.min(delay*2, MAX_DELAY);
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ex) {
                    break;
                }
            }
            SubscriptionHandle subscriptionHandle;
            synchronized (this) {
                subscriptionHandle = this.subscriptionHandle;
            }
            if (subscriptionHandle == null) {
                break;
            }
            try {
                Notification[] notifications = service.pullNotifications(subscriptionHandle, 20);
                synchronized (this) {
                    for (NotificationListenerRegistration registration : registrations) {
                        executor.execute(() -> {
                            for (Notification notification : notifications) {
                                if (registration.getSelector().isNotificationEnabled(notification)) {
                                    registration.handleNotification(notification);
                                }
                            }
                        });
                    }
                }
                if (notifications.length > 0) {
                    doDelay = false;
                    // Reset delay for next time
                    delay = MIN_DELAY;
                } else {
                    doDelay = true;
                }
            } catch (SubscriptionNotFoundException ex) {
                synchronized (this) {
                    // If the handle changed, then somebody else has reregistered the subscription already
                    if (subscriptionHandle == this.subscriptionHandle) {
                        try {
                            this.subscriptionHandle = service.addSubscription(subscriptionInfo, null);
                        } catch (IOException ex2) {
                            log.warn("Failed to renew subscription " + subscriptionHandle, ex2);
                        }
                    }
                }
            } catch (IOException ex) {
                log.warn("Failed to retrieve notifications for subscription " + subscriptionHandle, ex);
                doDelay = true;
            }
        }
    }

    public synchronized void closing() {
        registrations.clear();
        try {
            reconcileSubscriptions();
        } catch (Exception ex) {
            log.error("Failed to remove subscription", ex);
        }
        subscriptionHandle = null;
        subscriptionInfo = null;
        registrations.clear();
    }
}
