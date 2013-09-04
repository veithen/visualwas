package com.github.veithen.visualwas.connector.notification;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.github.veithen.visualwas.connector.mapped.MappedClass;

@MappedClass("com.ibm.ws.management.event.ConsolidatedFilter")
public final class SubscriptionInfo implements Serializable {
    private static final long serialVersionUID = 8448046386485560623L;
    
    private final Set<NotificationSelector> filters = Collections.synchronizedSet(new HashSet<NotificationSelector>());

    public void addSelector(NotificationSelector selector) {
        filters.add(selector);
    }
}
