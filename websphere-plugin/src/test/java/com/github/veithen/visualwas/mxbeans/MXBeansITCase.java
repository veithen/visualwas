/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.github.veithen.visualwas.mxbeans;

import static com.google.common.truth.Truth.assertThat;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.junit.Test;

import com.github.veithen.visualwas.jmx.soap.SOAPJMXConnector;

public class MXBeansITCase {
    @Test
    public void test() throws Exception {
        Map<String,Object> env = new HashMap<String,Object>();
        env.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, "com.github.veithen.visualwas.jmx");
        env.put(SOAPJMXConnector.PROXY, Proxy.NO_PROXY);
        env.put(JMXConnector.CREDENTIALS, new String[] { "wsadmin", "abcd1234" });
        env.put(SOAPJMXConnector.TRUST_MANAGER, new PromiscuousTrustManager());
        JMXServiceURL url = new JMXServiceURL("soap", "localhost", Integer.parseInt(System.getProperty("was.soapPort")));
        JMXConnector connector = JMXConnectorFactory.connect(url, env);
        MBeanServerConnection connection = connector.getMBeanServerConnection();
        RuntimeMXBean runtimeMXBean = JMX.newMXBeanProxy(connection, new ObjectName(ManagementFactory.RUNTIME_MXBEAN_NAME), RuntimeMXBean.class);
        Map<String,String> systemProperties = runtimeMXBean.getSystemProperties();
        assertThat(systemProperties).containsEntry("java.util.logging.manager", "com.ibm.ws.bootstrap.WsLogManager");
        connector.close();
    }
}
