/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 Andreas Veithen
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
        env.put(SOAPJMXConnector.CLASS_LOADER_PROVIDER, WebSphereClassLoaderProvider.getInstance());
        return env;
    }
}
