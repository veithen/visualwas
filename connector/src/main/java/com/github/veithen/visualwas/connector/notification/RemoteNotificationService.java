package com.github.veithen.visualwas.connector.notification;

import java.io.IOException;

import javax.management.Notification;

import com.github.veithen.visualwas.connector.Operation;
import com.github.veithen.visualwas.connector.Param;
import com.github.veithen.visualwas.connector.description.AdminServiceDescription;
import com.github.veithen.visualwas.connector.description.AdminServiceDescriptionFactory;

public interface RemoteNotificationService {
    AdminServiceDescription DESCRIPTION = AdminServiceDescriptionFactory.getInstance().createDescription(RemoteNotificationService.class);
    
    @Operation(name="addNotificationListener")
    SubscriptionHandle addSubscription(@Param(name="filter") SubscriptionInfo subscriptionInfo,
                                       @Param(name="listener") PushNotificationListener listener) throws IOException;

    @Operation(name="removeNotificationListener")
    void removeSubscription(@Param(name="listenerId") SubscriptionHandle handle) throws SubscriptionNotFoundException, IOException;

    @Operation(name="resetFilter")
    void updateSubscription(@Param(name="listenerId") SubscriptionHandle handle,
                            @Param(name="filter") SubscriptionInfo subscriptionInfo) throws SubscriptionNotFoundException, IOException;

    Notification[] pullNotifications(@Param(name="id") SubscriptionHandle handle,
                                     @Param(name="batchSize") Integer batchSize) throws SubscriptionNotFoundException, IOException;
}
