/*
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
package com.github.veithen.visualwas.mxbeans;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Constructor;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.net.ssl.SSLHandshakeException;

import org.graalvm.visualvm.application.Application;
import org.graalvm.visualvm.application.jvm.Jvm;
import org.graalvm.visualvm.application.jvm.JvmFactory;
import org.graalvm.visualvm.application.jvm.MonitoredData;
import org.graalvm.visualvm.core.datasupport.Stateful;
import org.graalvm.visualvm.jmx.JmxApplicationsSupport;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openide.modules.ModuleInstall;

import com.github.veithen.visualwas.env.CustomWebSphereEnvironmentProvider;
import com.github.veithen.visualwas.env.EnvUtil;
import com.github.veithen.visualwas.trust.NotTrustedException;
import com.github.veithen.visualwas.trust.TrustStore;

public class VisualVMITCase {
    @TempDir static File netbeansUserDir;

    private static void installModule(String installerClassName) throws Exception {
        Constructor<? extends ModuleInstall> installer =
                Class.forName(installerClassName)
                        .asSubclass(ModuleInstall.class)
                        .getDeclaredConstructor();
        installer.setAccessible(true);
        installer.newInstance().restored();
    }

    @BeforeAll
    static void before() throws Exception {
        System.setProperty("netbeans.user", netbeansUserDir.getAbsolutePath());
        installModule("org.graalvm.visualvm.jmx.Installer");
        installModule("org.graalvm.visualvm.jvm.Installer");
    }

    @AfterAll
    static void after() {
        System.getProperties().remove("netbeans.user");
    }

    static Stream<Arguments> provideArguments() {
        return Stream.of(
                Arguments.of(
                        "monitor",
                        new String[] {"dumpOnOOMEnabled", "takeHeapDump", "takeThreadDump"}),
                Arguments.of("operator", new String[] {"dumpOnOOMEnabled", "takeHeapDump"}));
    }

    @ParameterizedTest
    @MethodSource("provideArguments")
    @Timeout(value = 2, unit = TimeUnit.MINUTES)
    public void test(String role, String[] expectedUnsupportedFeatures) throws Exception {
        JMXServiceURL url =
                new JMXServiceURL(
                        "soap", "localhost", Integer.parseInt(System.getProperty("was.soapPort")));
        String password = "changeme";

        // Automatically add the server certificate to the trust store
        TrustStore trustStore = TrustStore.getInstance();
        Map<String, Object> env = EnvUtil.createEnvironment(true);
        env.put(JMXConnector.CREDENTIALS, new String[] {role, password});
        try {
            JMXConnectorFactory.connect(url, env);
            fail("Expected exception");
        } catch (SSLHandshakeException ex) {
            Throwable cause = ex.getCause();
            assertThat(cause).isInstanceOf(NotTrustedException.class);
            X509Certificate[] chain = ((NotTrustedException) ex.getCause()).getChain();
            trustStore.addCertificate(chain[chain.length - 1]);
        }

        try {
            Application app =
                    JmxApplicationsSupport.getInstance()
                            .createJmxApplication(
                                    url.toString(),
                                    "test",
                                    new CustomWebSphereEnvironmentProvider(
                                            role, password.toCharArray(), false),
                                    false);
            assertThat(app).isNotNull();
            try {
                Object lock = new Object();
                app.addPropertyChangeListener(
                        Stateful.PROPERTY_STATE,
                        (evt) -> {
                            synchronized (lock) {
                                lock.notifyAll();
                            }
                        });
                synchronized (lock) {
                    while (app.getState() != Stateful.STATE_AVAILABLE) {
                        lock.wait();
                    }
                }

                // There is a problem in VisualVM 2.0.1 that can cause deadlock. Sleep a bit to
                // avoid that.
                Thread.sleep(250);

                Jvm jvm = JvmFactory.getJVMFor(app);
                assertThat(jvm).isNotNull();

                Set<String> unsupportedFeatures = new HashSet<>();
                for (PropertyDescriptor prop :
                        Introspector.getBeanInfo(Jvm.class).getPropertyDescriptors()) {
                    String name = prop.getName();
                    if (name.endsWith("Supported") && !(Boolean) prop.getReadMethod().invoke(jvm)) {
                        unsupportedFeatures.add(name.substring(0, name.length() - 9));
                    }
                }
                assertThat(unsupportedFeatures)
                        .containsExactlyElementsIn(expectedUnsupportedFeatures);

                MonitoredData data = jvm.getMonitoredData();
                assertThat(data.getLoadedClasses()).isGreaterThan(5000L);
            } finally {
                app.getHost().getRepository().removeDataSource(app);
            }
        } finally {
            trustStore.clear();
        }
    }
}
