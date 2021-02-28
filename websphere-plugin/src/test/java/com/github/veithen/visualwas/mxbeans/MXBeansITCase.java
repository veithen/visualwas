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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.net.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.management.JMX;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.junit.jupiter.api.Test;

import com.github.veithen.visualwas.jmx.soap.SOAPJMXConnector;
import com.github.veithen.visualwas.x509.PromiscuousTrustManager;

public class MXBeansITCase {
    private interface Action<T> {
        void run(T object) throws Exception;
    }

    private static <T> void run(String role, Action<MBeanServerConnection> action)
            throws Exception {
        Map<String, Object> env = new HashMap<String, Object>();
        env.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, "com.github.veithen.visualwas.jmx");
        env.put(SOAPJMXConnector.PROXY, Proxy.NO_PROXY);
        env.put(JMXConnector.CREDENTIALS, new String[] {role, "changeme"});
        env.put(SOAPJMXConnector.TRUST_MANAGER, PromiscuousTrustManager.INSTANCE);
        JMXServiceURL url =
                new JMXServiceURL(
                        "soap", "localhost", Integer.parseInt(System.getProperty("was.soapPort")));
        JMXConnector connector = JMXConnectorFactory.connect(url, env);
        MBeanServerConnection connection = connector.getMBeanServerConnection();
        action.run(connection);
        connector.close();
    }

    private static <T> void run(
            String role, String objectName, Class<T> interfaceClass, Action<T> action)
            throws Exception {
        run(
                role,
                connection ->
                        action.run(
                                JMX.newMXBeanProxy(
                                        connection, new ObjectName(objectName), interfaceClass)));
    }

    @Test
    public void testAccessRulesCompleteness() throws Exception {
        run(
                "monitor",
                connection -> {
                    Set<String> keys = new HashSet<>();
                    for (ObjectName mxbean :
                            connection.queryNames(new ObjectName("java.lang:*"), null)) {
                        String type = mxbean.getKeyProperty("type");
                        MBeanInfo mbeanInfo = connection.getMBeanInfo(mxbean);
                        for (MBeanOperationInfo operationInfo : mbeanInfo.getOperations()) {
                            keys.add(type + "." + operationInfo.getName());
                        }
                        for (MBeanAttributeInfo attributeInfo : mbeanInfo.getAttributes()) {
                            String name = attributeInfo.getName();
                            if (name.equals("ObjectName")) {
                                continue;
                            }
                            if (attributeInfo.isReadable()) {
                                if (attributeInfo.isIs()) {
                                    keys.add(type + ".is" + name);
                                } else {
                                    keys.add(type + ".get" + name);
                                }
                            }
                            if (attributeInfo.isWritable()) {
                                keys.add(type + ".set" + name);
                            }
                        }
                    }
                    Properties accessProperties = new Properties();
                    InputStream in =
                            PlatformMXBeansRegistrant.class.getResourceAsStream(
                                    "access.properties");
                    accessProperties.load(in);
                    in.close();
                    assertThat(accessProperties.keySet()).containsAll(keys);
                });
    }

    @Test
    public void testRuntimeMXBean() throws Exception {
        run(
                "monitor",
                ManagementFactory.RUNTIME_MXBEAN_NAME,
                RuntimeMXBean.class,
                runtimeMXBean -> {
                    Map<String, String> systemProperties = runtimeMXBean.getSystemProperties();
                    assertThat(systemProperties)
                            .containsEntry(
                                    "java.util.logging.manager",
                                    "com.ibm.ws.bootstrap.WsLogManager");
                });
    }

    @Test
    public void testAccessDenied() {
        assertThrows(
                SecurityException.class,
                () ->
                        run(
                                "monitor",
                                ManagementFactory.MEMORY_MXBEAN_NAME,
                                MemoryMXBean.class,
                                MemoryMXBean::gc));
    }

    @Test
    public void testAccessGranted() throws Exception {
        run("operator", ManagementFactory.MEMORY_MXBEAN_NAME, MemoryMXBean.class, MemoryMXBean::gc);
    }
}
