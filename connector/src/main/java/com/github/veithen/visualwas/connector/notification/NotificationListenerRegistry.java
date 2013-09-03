package com.github.veithen.visualwas.connector.notification;

import java.io.IOException;

import javax.management.Notification;

import com.github.veithen.visualwas.connector.Param;
import com.github.veithen.visualwas.connector.description.AdminServiceDescription;
import com.github.veithen.visualwas.connector.description.AdminServiceDescriptionFactory;

public interface NotificationListenerRegistry {
    AdminServiceDescription DESCRIPTION = AdminServiceDescriptionFactory.getInstance().createDescription(NotificationListenerRegistry.class);
    
    ListenerIdentifier addNotificationListener(@Param(name="filter") ConsolidatedFilter filter,
                                               @Param(name="listener") PushNotificationListener listener) throws IOException;

    void removeNotificationListener(@Param(name="listenerId") ListenerIdentifier listenerId) throws ReceiverNotFoundException, IOException;

    void resetFilter(@Param(name="listenerId") ListenerIdentifier listenerId,
                     @Param(name="filter") ConsolidatedFilter filter) throws ReceiverNotFoundException, IOException;

    Notification[] pullNotifications(@Param(name="id") ListenerIdentifier id,
                                     @Param(name="batchSize") Integer batchSize) throws ReceiverNotFoundException, IOException;
}
