package com.github.veithen.visualwas.jmx.soap;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.management.ListenerNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.remote.JMXConnector;
import javax.security.auth.Subject;

import com.github.veithen.visualwas.connector.AdminService;
import com.github.veithen.visualwas.connector.AdminServiceFactory;
import com.github.veithen.visualwas.connector.transport.DefaultTransport;

public class SOAPJMXConnector implements JMXConnector {
    private final String host;
    private final int port;
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
    public void connect(Map<String, ?> env) throws IOException {
        // TODO: use HTTPS if security is enabled
        adminService = AdminServiceFactory.getInstance().createAdminService(new DefaultTransport(new URL("http", host, port, "/")));
        // TODO: we should call isAlive here and save the session ID (so that we can detect server restarts)
        adminService.getServerMBean();
    }

    @Override
    public MBeanServerConnection getMBeanServerConnection() throws IOException {
        return new AdminServiceMBeanServerConnection(adminService);
    }

    @Override
    public MBeanServerConnection getMBeanServerConnection(Subject delegationSubject)
            throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void addConnectionNotificationListener(NotificationListener listener,
            NotificationFilter filter, Object handback) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeConnectionNotificationListener(NotificationListener listener)
            throws ListenerNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeConnectionNotificationListener(NotificationListener l, NotificationFilter f,
            Object handback) throws ListenerNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public String getConnectionId() throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }
}
