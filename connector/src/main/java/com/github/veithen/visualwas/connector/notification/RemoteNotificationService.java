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

    /**
     * Poll the server for notifications for a given subscription. This call will block until at
     * least one notification is available. Note that an invocation of this method will also block a
     * thread in the <tt>SoapConnectorThreadPool</tt> on the server side. Therefore it is
     * recommended to group multiple {@link NotificationSelector} instances into a single
     * {@link SubscriptionInfo} instead of using multiple subscriptions.
     * 
     * @param handle
     *            the handle for the subscription
     * @param batchSize
     *            the maximum number of notifications to return
     * @return an array with notifications for the given subscription
     * @throws SubscriptionNotFoundException
     *             if the subscription with the given handle is not known by the server
     * @throws IOException
     */
    // TODO: this method assumes that there is no read timeout; if we make the read timeout configurable, then we should add an interceptor that overrides the read timeout for invocations of this method
    // TODO: can we get a ClassNotFoundException here?
    Notification[] pullNotifications(@Param(name="id") SubscriptionHandle handle,
                                     @Param(name="batchSize") Integer batchSize) throws SubscriptionNotFoundException, IOException;
}
