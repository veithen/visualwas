package com.github.veithen.visualwas.jmx.soap;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.management.ListenerNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.remote.JMXConnectionNotification;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.net.ssl.TrustManager;
import javax.security.auth.Subject;

import com.github.veithen.visualwas.connector.AdminService;
import com.github.veithen.visualwas.connector.AdminServiceFactory;
import com.github.veithen.visualwas.connector.Interceptor;
import com.github.veithen.visualwas.connector.loader.ClassLoaderProvider;
import com.github.veithen.visualwas.connector.loader.SimpleClassLoaderProvider;
import com.github.veithen.visualwas.connector.security.BasicAuthInterceptor;
import com.github.veithen.visualwas.connector.transport.DefaultTransport;

public class SOAPJMXConnector implements JMXConnector {
    private static final String ENV_PROP_PREFIX = "com.github.veithen.visualwas.jmx.soap.";
    
    public static final String TRUST_MANAGER = ENV_PROP_PREFIX + "trustManager";
    public static final String PROXY = ENV_PROP_PREFIX + "proxy";
    
    /**
     * Name of the attribute that specifies the class loader provider. The class loader provider
     * determines the class loader to use when deserializing values returned by WebSphere. The
     * attribute must be an instance of {@link ClassLoaderProvider}. If it is specified, then
     * {@link JMXConnectorFactory#DEFAULT_CLASS_LOADER} will be ignored. If neither
     * {@link #CLASS_LOADER_PROVIDER} nor {@link JMXConnectorFactory#DEFAULT_CLASS_LOADER} is
     * specified, then the connector will use the thread context class loader.
     */
    public static final String CLASS_LOADER_PROVIDER = ENV_PROP_PREFIX + "classLoaderProvider";
    
    private final String host;
    private final int port;
    private final Map<String,?> env;
    private final NotificationBroadcasterSupport connectionBroadcaster = new NotificationBroadcasterSupport();
    private long connectionNotificationSequence;
    private String connectionId;
    private AdminService adminService;

    public SOAPJMXConnector(String host, int port, Map<String,?> env) {
        this.host = host;
        this.port = port;
        this.env = env;
    }

    @Override
    public void connect() throws IOException {
        connect(null);
    }

    public void connect(Map<String,?> env) throws IOException {
        if (env == null) {
            internalConnect(this.env);
        } else {
            Map<String,Object> actualEnv = new HashMap<String,Object>(this.env);
            actualEnv.putAll(env);
            internalConnect(actualEnv);
        }
    }
    
    private synchronized void internalConnect(Map<String,?> env) throws IOException {
        connectionId = UUID.randomUUID().toString();
        // TODO: use HTTPS if security is enabled
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        interceptors.add(new ConnectionIdInterceptor(connectionId));
        String[] credentials = (String[])env.get(JMXConnector.CREDENTIALS);
        String protocol;
        if (credentials == null) {
            protocol = "http";
        } else {
            protocol = "https";
            interceptors.add(new BasicAuthInterceptor(credentials[0], credentials[1]));
        }
        ClassLoaderProvider classLoaderProvider = (ClassLoaderProvider)env.get(CLASS_LOADER_PROVIDER);
        if (classLoaderProvider == null) {
            ClassLoader cl = (ClassLoader)env.get(JMXConnectorFactory.DEFAULT_CLASS_LOADER);
            classLoaderProvider = cl == null ? ClassLoaderProvider.TCCL : new SimpleClassLoaderProvider(cl);
        }
        adminService = AdminServiceFactory.getInstance().createAdminService(
                interceptors.toArray(new Interceptor[interceptors.size()]),
                new DefaultTransport(new URL(protocol, host, port, "/"), (Proxy)env.get(PROXY), (TrustManager)env.get(TRUST_MANAGER)),
                classLoaderProvider);
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
