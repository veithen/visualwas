package com.github.veithen.visualwas.connector.notification;

import com.github.veithen.visualwas.connector.feature.Configurator;
import com.github.veithen.visualwas.connector.feature.Dependencies;
import com.github.veithen.visualwas.connector.feature.Feature;
import com.github.veithen.visualwas.connector.mapped.ClassMappingConfigurator;
import com.github.veithen.visualwas.connector.mapped.ClassMappingFeature;

@Dependencies(ClassMappingFeature.class)
public final class NotificationFeature implements Feature {
    public static final NotificationFeature INSTANCE = new NotificationFeature();
    
    private NotificationFeature() {}

    @Override
    public void configureConnector(Configurator configurator) {
        configurator.addAdminServiceDescription(NotificationListenerRegistry.DESCRIPTION);
        configurator.getAdapter(ClassMappingConfigurator.class).addMappedClasses(
                ConsolidatedFilter.class,
                PushNotificationListener.class,
                ListenerIdentifier.class,
                ReceiverNotFoundException.class);
    }
}
