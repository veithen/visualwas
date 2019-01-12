/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2019 Andreas Veithen
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
package com.github.veithen.visualwas.jmx.soap;

import java.io.IOException;
import java.net.Proxy;
import java.util.HashMap;
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

import com.github.veithen.visualwas.connector.Attributes;
import com.github.veithen.visualwas.connector.Connector;
import com.github.veithen.visualwas.connector.factory.ConnectorConfiguration;
import com.github.veithen.visualwas.connector.factory.ConnectorFactory;
import com.github.veithen.visualwas.connector.feature.Feature;
import com.github.veithen.visualwas.connector.loader.ClassLoaderProvider;
import com.github.veithen.visualwas.connector.loader.SimpleClassLoaderProvider;
import com.github.veithen.visualwas.connector.notification.NotificationDispatcher;
import com.github.veithen.visualwas.connector.notification.NotificationDispatcherFeature;
import com.github.veithen.visualwas.connector.security.BasicAuthCredentials;
import com.github.veithen.visualwas.connector.security.Credentials;
import com.github.veithen.visualwas.connector.transport.Endpoint;
import com.github.veithen.visualwas.connector.transport.TransportConfiguration;

public class SOAPJMXConnector implements JMXConnector {
    private static final String ENV_PROP_PREFIX = "com.github.veithen.visualwas.jmx.soap.";
    
    // TODO: should we have a single TRANSPORT_OPTIONS attribute?
    public static final String TRUST_MANAGER = ENV_PROP_PREFIX + "trustManager";
    public static final String PROXY = ENV_PROP_PREFIX + "proxy";
    
    /**
     * Name of the attribute that specifies the connect timeout. The value must be an
     * {@link Integer} representing the timeout in milliseconds.
     */
    public static final String CONNECT_TIMEOUT = ENV_PROP_PREFIX + "connectTimeout";
    
    /**
     * Name of the attribute that specifies the class loader provider. The class loader provider
     * determines the class loader to use when deserializing values returned by WebSphere. The
     * attribute must be an instance of {@link ClassLoaderProvider}. If it is specified, then
     * {@link JMXConnectorFactory#DEFAULT_CLASS_LOADER} will be ignored. If neither
     * {@link #CLASS_LOADER_PROVIDER} nor {@link JMXConnectorFactory#DEFAULT_CLASS_LOADER} is
     * specified, then the connector will use the thread context class loader.
     */
    public static final String CLASS_LOADER_PROVIDER = ENV_PROP_PREFIX + "classLoaderProvider";
    
    /**
     * An array of {@link Feature features} to install.
     */
    public static final String FEATURES = ENV_PROP_PREFIX + "features";
    
    /**
     * Name of the attribute that specifies the exception transformer. The value must be an instance
     * of {@link ExceptionTransformer}.
     */
    public static final String EXCEPTION_TRANSFORMER = ENV_PROP_PREFIX + "exceptionTransformer";
    
    private final String host;
    private final int port;
    private final Map<String,?> env;
    private final NotificationBroadcasterSupport connectionBroadcaster = new NotificationBroadcasterSupport();
    private long connectionNotificationSequence;
    private String connectionId;
    private Connector connector;
    private ExceptionTransformer exceptionTransformer;
    private boolean connected;

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
            Map<String,Object> actualEnv = new HashMap<>(this.env);
            actualEnv.putAll(env);
            internalConnect(actualEnv);
        }
    }
    
    private synchronized void internalConnect(Map<String,?> env) throws IOException {
        connectionId = UUID.randomUUID().toString();
        Attributes.Builder attributes = Attributes.builder();
        String[] jmxCredentials = (String[])env.get(JMXConnector.CREDENTIALS);
        if (jmxCredentials != null) {
            attributes.set(Credentials.class, new BasicAuthCredentials(jmxCredentials[0], jmxCredentials[1]));
        }
        TransportConfiguration.Builder transportConfigBuilder = TransportConfiguration.custom();
        transportConfigBuilder.setProxy((Proxy)env.get(PROXY));
        Integer connectTimeout = (Integer)env.get(CONNECT_TIMEOUT);
        if (connectTimeout != null) {
            transportConfigBuilder.setConnectTimeout(connectTimeout);
        }
        transportConfigBuilder.setTrustManager((TrustManager)env.get(TRUST_MANAGER));
        ClassLoaderProvider classLoaderProvider = (ClassLoaderProvider)env.get(CLASS_LOADER_PROVIDER);
        if (classLoaderProvider == null) {
            ClassLoader cl = (ClassLoader)env.get(JMXConnectorFactory.DEFAULT_CLASS_LOADER);
            classLoaderProvider = cl == null ? ClassLoaderProvider.TCCL : new SimpleClassLoaderProvider(cl);
        }
        ConnectorConfiguration.Builder connectorConfigBuilder = ConnectorConfiguration.custom();
        connectorConfigBuilder.setClassLoaderProvider(classLoaderProvider);
        connectorConfigBuilder.setTransportConfiguration(transportConfigBuilder.build());
        connectorConfigBuilder.addFeatures(new ConnectionIdFeature(connectionId), NotificationDispatcherFeature.INSTANCE);
        Feature[] features = (Feature[])env.get(FEATURES);
        if (features != null) {
            connectorConfigBuilder.addFeatures(features);
        }
        connector = ConnectorFactory.getInstance().createConnector(
                new Endpoint(host, port, jmxCredentials != null),
                connectorConfigBuilder.build(),
                attributes.build());
        exceptionTransformer = (ExceptionTransformer)env.get(EXCEPTION_TRANSFORMER);
        if (exceptionTransformer == null) {
            exceptionTransformer = ExceptionTransformer.DEFAULT;
        }
        try {
            // TODO: we should call isAlive here and save the session ID (so that we can detect server restarts)
            connector.getServerMBean();
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
        connected = true;
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
        return new MBeanServerConnectionImpl(connector, connector.getAdapter(NotificationDispatcher.class), exceptionTransformer);
    }

    @Override
    public synchronized MBeanServerConnection getMBeanServerConnection(Subject delegationSubject)
            throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void close() throws IOException {
        if (connected) {
            connector.close();
            connectionBroadcaster.sendNotification(new JMXConnectionNotification(
                    JMXConnectionNotification.CLOSED,
                    this,
                    connectionId,
                    connectionNotificationSequence++,
                    "Connection closed",
                    null));
            connected = false;
        }
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
