package com.github.veithen.visualwas.jmx.soap;

import java.io.IOException;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorProvider;
import javax.management.remote.JMXServiceURL;

public class ClientProvider implements JMXConnectorProvider {
    public JMXConnector newJMXConnector(JMXServiceURL serviceURL, Map<String,?> environment) throws IOException {
        return new SOAPJMXConnector(serviceURL.getHost(), serviceURL.getPort());
    }
}
