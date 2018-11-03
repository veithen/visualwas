/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2018 Andreas Veithen
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.remote.JMXConnectorFactory;

import com.github.veithen.visualwas.client.config.ConfigClientFeature;
import com.github.veithen.visualwas.client.jsr77.JSR77ClientFeature;
import com.github.veithen.visualwas.client.pmi.PmiClientFeature;
import com.github.veithen.visualwas.client.ras.RasLoggingFeature;
import com.github.veithen.visualwas.client.repository.RepositoryClientFeature;
import com.github.veithen.visualwas.connector.feature.Feature;
import com.github.veithen.visualwas.connector.federation.DisableFederationFeature;
import com.github.veithen.visualwas.jmx.soap.SOAPJMXConnector;
import com.github.veithen.visualwas.trust.TrustStore;

public final class EnvUtil {
    private EnvUtil() {}
    
    public static Map<String,Object> createEnvironment(boolean securityEnabled, boolean federationDisabled) {
        Map<String,Object> env = new HashMap<>();
        env.put(JMXConnectorFactory.PROTOCOL_PROVIDER_CLASS_LOADER, SOAPJMXConnector.class.getClassLoader());
        env.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, "com.github.veithen.visualwas.jmx");
        // In VisualVM, one typically configures the HTTP proxy to download new plugins and updates,
        // i.e. to connect to the Internet. SOAP connections to WebSphere should not pass through that proxy.
        env.put(SOAPJMXConnector.PROXY, Proxy.NO_PROXY);
        if (securityEnabled) {
            env.put(SOAPJMXConnector.TRUST_MANAGER, TrustStore.getInstance().createTrustManager());
        }
        WebSphereClassLoaderProvider classLoaderProvider = WebSphereClassLoaderProvider.getInstance();
        env.put(SOAPJMXConnector.CLASS_LOADER_PROVIDER, classLoaderProvider);
        List<Feature> features = new ArrayList<>();
        if (federationDisabled) {
            features.add(DisableFederationFeature.INSTANCE);
        }
        features.addAll(Arrays.asList(
                ConfigClientFeature.INSTANCE,
                JSR77ClientFeature.INSTANCE,
                PmiClientFeature.INSTANCE,
                RepositoryClientFeature.INSTANCE,
                RasLoggingFeature.INSTANCE));
        env.put(SOAPJMXConnector.FEATURES, features.toArray(new Feature[features.size()]));
        return env;
    }
}
