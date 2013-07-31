/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.veithen.visualwas;

import com.sun.tools.visualvm.application.Application;
import com.sun.tools.visualvm.core.datasource.Storage;
import com.sun.tools.visualvm.jmx.EnvironmentProvider;
import java.util.HashMap;
import java.util.Map;
import javax.management.remote.JMXConnectorFactory;

/**
 *
 * @author veithen
 */
public class WebSphereEnvironmentProvider extends EnvironmentProvider {
    @Override
    public Map<String,?> getEnvironment(Application application, Storage storage) {
        Map<String,Object> env = new HashMap<String,Object>();
        env.put(JMXConnectorFactory.PROTOCOL_PROVIDER_CLASS_LOADER, WebSphereEnvironmentProvider.class.getClassLoader());
        env.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, "com.github.veithen.visualwas.jmx");
        return env;
    }
}
