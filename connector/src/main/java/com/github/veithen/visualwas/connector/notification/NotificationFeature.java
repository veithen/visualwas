package com.github.veithen.visualwas.connector.notification;

import com.github.veithen.visualwas.connector.feature.Configurator;
import com.github.veithen.visualwas.connector.feature.Feature;

public final class NotificationFeature implements Feature {
    public static final NotificationFeature INSTANCE = new NotificationFeature();
    
    private NotificationFeature() {}

    @Override
    public void configureConnector(Configurator configurator) {
        configurator.addAdminServiceDescription(NotificationListenerRegistry.DESCRIPTION);
    }
}
