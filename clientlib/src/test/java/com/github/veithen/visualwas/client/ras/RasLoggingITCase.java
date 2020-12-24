/*-
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
package com.github.veithen.visualwas.client.ras;

import static com.google.common.truth.Truth.assertThat;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import org.junit.Test;

import com.github.veithen.visualwas.client.WebSphereITCase;
import com.github.veithen.visualwas.connector.feature.Feature;
import com.github.veithen.visualwas.connector.notification.NotificationDispatcher;
import com.github.veithen.visualwas.connector.notification.NotificationDispatcherFeature;

public class RasLoggingITCase extends WebSphereITCase {
    @Override
    protected Feature[] getFeatures() {
        return new Feature[] {RasLoggingFeature.INSTANCE, NotificationDispatcherFeature.INSTANCE};
    }

    @Test
    public void test() throws Exception {
        ObjectName rasLoggingService =
                connector
                        .queryNames(new ObjectName("WebSphere:type=RasLoggingService,*"), null)
                        .iterator()
                        .next();
        ObjectName applicationManager =
                connector
                        .queryNames(new ObjectName("WebSphere:type=ApplicationManager,*"), null)
                        .iterator()
                        .next();
        NotificationDispatcher dispatcher = connector.getAdapter(NotificationDispatcher.class);
        BlockingQueue<RasMessage> messages = new LinkedBlockingQueue<>();
        NotificationListener listener =
                new NotificationListener() {
                    @Override
                    public void handleNotification(Notification notification, Object handback) {
                        try {
                            messages.put((RasMessage) notification.getUserData());
                        } catch (InterruptedException ex) {
                            Thread.interrupted();
                        }
                    }
                };
        dispatcher.addNotificationListener(rasLoggingService, listener, null, null);
        connector.invokeAsync(
                applicationManager,
                "stopApplication",
                new Object[] {"query"},
                new String[] {String.class.getName()});
        while (true) {
            RasMessage message = messages.poll(5, TimeUnit.SECONDS);
            assertThat(message).isNotNull();
            if (message.getMessageKey().equals("WSVR0220I")) {
                break;
            }
        }
        dispatcher.removeNotificationListener(rasLoggingService, listener, null, null);
        // Drain remaining notifications.
        while (messages.poll(2, TimeUnit.SECONDS) != null) {
            // Just loop
        }
        connector.invoke(
                applicationManager,
                "startApplication",
                new Object[] {"query"},
                new String[] {String.class.getName()});
        // No further messages should have been delivered to the listener.
        assertThat(messages.poll(5, TimeUnit.SECONDS)).isNull();
    }
}
