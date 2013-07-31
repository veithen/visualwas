package com.github.veithen.visualwas.jmx.soap;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.ListenerNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.remote.JMXConnectionNotification;
import javax.management.remote.JMXConnector;
import javax.security.auth.Subject;

import com.github.veithen.visualwas.connector.AdminService;
import com.github.veithen.visualwas.connector.AdminServiceFactory;
import com.github.veithen.visualwas.connector.transport.DefaultTransport;

public class SOAPJMXConnector implements JMXConnector {
    private static final AtomicInteger nextConnectionId = new AtomicInteger();
    
    private final String host;
    private final int port;
    private final NotificationBroadcasterSupport connectionBroadcaster = new NotificationBroadcasterSupport();
    private long connectionNotificationSequence;
    private String connectionId;
    private AdminService adminService;

    public SOAPJMXConnector(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void connect() throws IOException {
        connect(null);
    }

    @Override
    public synchronized void connect(Map<String, ?> env) throws IOException {
        connectionId = String.valueOf(nextConnectionId.getAndIncrement());
        // TODO: use HTTPS if security is enabled
        adminService = AdminServiceFactory.getInstance().createAdminService(new DefaultTransport(new URL("http", host, port, "/")));
        try {
            // TODO: we should call isAlive here and save the session ID (so that we can detect server restarts)
            adminService.getServerMBean();
        } catch (IOException ex) {
            connectionBroadcaster.sendNotification(new JMXConnectionNotification(
                    JMXConnectionNotification.FAILED,
                    this,
                    connectionId,
                    connectionNotificationSequence++,
                    "Connection failure",
                    ex));
            throw ex;
        }
        connectionBroadcaster.sendNotification(new JMXConnectionNotification(
                JMXConnectionNotification.OPENED,
                this,
                connectionId,
                connectionNotificationSequence++,
                "Successfully connected",
                null));
    }

    @Override
    public synchronized MBeanServerConnection getMBeanServerConnection() throws IOException {
        return new AdminServiceMBeanServerConnection(adminService);
    }

    @Override
    public synchronized MBeanServerConnection getMBeanServerConnection(Subject delegationSubject)
            throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void close() throws IOException {
        connectionBroadcaster.sendNotification(new JMXConnectionNotification(
                JMXConnectionNotification.CLOSED,
                this,
                connectionId,
                connectionNotificationSequence++,
                "Connection closed",
                null));
    }

    @Override
    public void addConnectionNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) {
        connectionBroadcaster.addNotificationListener(listener, filter, handback);
    }

    @Override
    public void removeConnectionNotificationListener(NotificationListener listener) throws ListenerNotFoundException {
        connectionBroadcaster.removeNotificationListener(listener);
    }

    @Override
    public void removeConnectionNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) throws ListenerNotFoundException {
        connectionBroadcaster.removeNotificationListener(listener, filter, handback);
    }

    @Override
    public synchronized String getConnectionId() throws IOException {
        return connectionId;
    }
}
