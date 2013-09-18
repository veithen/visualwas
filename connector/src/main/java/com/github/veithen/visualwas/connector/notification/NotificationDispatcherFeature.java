package com.github.veithen.visualwas.connector.notification;

import com.github.veithen.visualwas.connector.AdminService;
import com.github.veithen.visualwas.connector.feature.AdapterFactory;
import com.github.veithen.visualwas.connector.feature.Configurator;
import com.github.veithen.visualwas.connector.feature.Dependencies;
import com.github.veithen.visualwas.connector.feature.Feature;

@Dependencies(RemoteNotificationFeature.class)
public class NotificationDispatcherFeature implements Feature {
    private final boolean autoReregister;
    
    public NotificationDispatcherFeature(boolean autoReregister) {
        this.autoReregister = autoReregister;
    }

    @Override
    public void configureConnector(Configurator configurator) {
        configurator.registerAdminServiceAdapter(NotificationDispatcher.class, new AdapterFactory<NotificationDispatcher>() {
            @Override
            public NotificationDispatcher createAdapter(AdminService adminService) {
                return new NotificationDispatcherImpl((RemoteNotificationService)adminService, autoReregister);
            }
        });
    }
}
