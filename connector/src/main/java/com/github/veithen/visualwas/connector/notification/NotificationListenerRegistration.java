package com.github.veithen.visualwas.connector.notification;

import javax.management.Notification;
import javax.management.NotificationListener;

final class NotificationListenerRegistration {
    private final NotificationSelector selector;
    private final NotificationListener listener;
    private final Object handback;
    
    NotificationListenerRegistration(NotificationSelector selector, NotificationListener listener, Object handback) {
        this.selector = selector;
        this.listener = listener;
        this.handback = handback;
    }

    NotificationSelector getSelector() {
        return selector;
    }

    void handleNotification(Notification notification) {
        listener.handleNotification(notification, handback);
    }
}
