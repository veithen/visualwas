package com.github.veithen.visualwas.connector.notification;

import com.github.veithen.visualwas.connector.description.AdminServiceDescription;
import com.github.veithen.visualwas.connector.description.AdminServiceDescriptionFactory;

public interface NotificationListenerRegistry {
    AdminServiceDescription DESCRIPTION = AdminServiceDescriptionFactory.getInstance().createDescription(NotificationListenerRegistry.class);
    

}
