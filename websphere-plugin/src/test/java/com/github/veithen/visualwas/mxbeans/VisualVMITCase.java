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
package com.github.veithen.visualwas.mxbeans;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.net.ssl.SSLHandshakeException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openide.modules.ModuleInstall;

import com.github.veithen.visualwas.env.CustomWebSphereEnvironmentProvider;
import com.github.veithen.visualwas.env.EnvUtil;
import com.github.veithen.visualwas.trust.NotTrustedException;
import com.github.veithen.visualwas.trust.TrustStore;
import com.sun.tools.visualvm.application.jvm.Jvm;
import com.sun.tools.visualvm.application.jvm.MonitoredData;
import com.sun.tools.visualvm.host.Host;
import com.sun.tools.visualvm.jmx.impl.JmxApplication;
import com.sun.tools.visualvm.jvm.JvmProvider;

public class VisualVMITCase {
    private static final Set<String> unsupportedFeatures = new HashSet<>(Arrays.asList(
            "dumpOnOOMEnabledSupported", "takeHeapDumpSupported"));

    @Rule
    public final TemporaryFolder netbeansUserDir = new TemporaryFolder() {
        @Override
        protected void before() throws Throwable {
            super.before();
            System.setProperty("netbeans.user", getRoot().getAbsolutePath());
        }

        @Override
        protected void after() {
            System.getProperties().remove("netbeans.user");
            super.after();
        }
    };

    @Test
    public void test() throws Exception {
        Constructor<? extends ModuleInstall> installer = Class.forName("com.sun.tools.visualvm.jmx.Installer").asSubclass(ModuleInstall.class).getDeclaredConstructor();
        installer.setAccessible(true);
        installer.newInstance().restored();
        JMXServiceURL url = new JMXServiceURL("soap", "localhost", Integer.parseInt(System.getProperty("was.soapPort")));
        String user = "monitor";
        String password = "changeme";

        // Automatically add the server certificate to the trust store
        Map<String,Object> env = EnvUtil.createEnvironment(true);
        env.put(JMXConnector.CREDENTIALS, new String[] { user, password });
        try {
            JMXConnectorFactory.connect(url, env);
            fail("Expected exception");
        } catch (SSLHandshakeException ex) {
            Throwable cause = ex.getCause();
            assertThat(cause).isInstanceOf(NotTrustedException.class);
            X509Certificate[] chain = ((NotTrustedException)ex.getCause()).getChain();
            TrustStore.getInstance().addCertificate(chain[chain.length-1]);
        }

        JmxApplication app = new JmxApplication(Host.LOCALHOST, url, new CustomWebSphereEnvironmentProvider(user, password.toCharArray(), false), null);
        Jvm jvm = new JvmProvider().createModelFor(app);

        for (PropertyDescriptor prop : Introspector.getBeanInfo(Jvm.class).getPropertyDescriptors()) {
            String name = prop.getName();
            if (name.endsWith("Supported") && !unsupportedFeatures.contains(name)) {
                assertThat((Boolean)prop.getReadMethod().invoke(jvm)).named("%s", name).isTrue();
            }
        }

        MonitoredData data = jvm.getMonitoredData();
        assertThat(data.getLoadedClasses()).isGreaterThan(5000L);
    }
}
