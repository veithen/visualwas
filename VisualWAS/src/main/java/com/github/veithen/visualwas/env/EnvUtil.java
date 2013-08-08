package com.github.veithen.visualwas.env;

import java.net.Proxy;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import javax.management.remote.JMXConnectorFactory;

import com.github.veithen.visualwas.jmx.soap.SOAPJMXConnector;
import com.github.veithen.visualwas.trust.TrustStore;

public final class EnvUtil {
    private EnvUtil() {}
    
    public static Map<String,Object> createEnvironment(boolean securityEnabled) {
        Map<String,Object> env = new HashMap<String,Object>();
        env.put(JMXConnectorFactory.PROTOCOL_PROVIDER_CLASS_LOADER, SOAPJMXConnector.class.getClassLoader());
        env.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, "com.github.veithen.visualwas.jmx");
        // In VisualVM, one typically configures the HTTP proxy to download new plugins and updates,
        // i.e. to connect to the Internet. SOAP connections to WebSphere should not pass through that proxy.
        env.put(SOAPJMXConnector.PROXY, Proxy.NO_PROXY);
        if (securityEnabled) {
            try {
                env.put(SOAPJMXConnector.TRUST_MANAGER, TrustStore.getInstance().createTrustManager());
            } catch (GeneralSecurityException ex) {
                // TODO: log this error somehow
                ex.printStackTrace();
            }
        }
        return env;
    }
}
