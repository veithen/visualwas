package com.github.veithen.visualwas;

import java.net.Proxy;
import java.security.GeneralSecurityException;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;

import com.github.veithen.visualwas.jmx.soap.SOAPJMXConnector;
import com.github.veithen.visualwas.trust.TrustStore;

public final class JMXUtil {
    private JMXUtil() {}
    
    public static void initEnvironment(Map<String,Object> env) {
        env.put(JMXConnectorFactory.PROTOCOL_PROVIDER_CLASS_LOADER, WebSphereEnvironmentProvider.class.getClassLoader());
        env.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, "com.github.veithen.visualwas.jmx");
        // In VisualVM, one typically configures the HTTP proxy to download new plugins and updates,
        // i.e. to connect to the Internet. SOAP connections to WebSphere should not pass through that proxy.
        env.put(SOAPJMXConnector.PROXY, Proxy.NO_PROXY);
    }
    
    public static void setCredentials(Map<String,Object> env, String username, String password) {
        env.put(JMXConnector.CREDENTIALS, new String[] { username, password });
        try {
            env.put(SOAPJMXConnector.TRUST_MANAGER, TrustStore.getInstance().createTrustManager());
        } catch (GeneralSecurityException ex) {
            // TODO: log this error somehow
            ex.printStackTrace();
        }
    }
}
