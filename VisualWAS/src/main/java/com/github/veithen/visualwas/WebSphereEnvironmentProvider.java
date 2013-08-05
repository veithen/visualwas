/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.veithen.visualwas;

import java.util.HashMap;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;

import com.sun.tools.visualvm.application.Application;
import com.sun.tools.visualvm.core.datasource.Storage;
import com.sun.tools.visualvm.core.datasupport.Utils;
import com.sun.tools.visualvm.jmx.EnvironmentProvider;

/**
 *
 * @author veithen
 */
public class WebSphereEnvironmentProvider extends EnvironmentProvider {
    private static final String PROPERTY_USERNAME = "prop_credentials_username";
    private static final String PROPERTY_PASSWORD = "prop_credentials_password";
    
    private String username;
    private String password;
    private boolean persistent;
    
    public WebSphereEnvironmentProvider() {
    }
    
    public WebSphereEnvironmentProvider(String username, String password, boolean persistent) {
        this.username = username;
        this.password = password;
        this.persistent = persistent;
    }
    
    @Override
    public Map<String,?> getEnvironment(Application application, Storage storage) {
        Map<String,Object> env = new HashMap<String,Object>();
        env.put(JMXConnectorFactory.PROTOCOL_PROVIDER_CLASS_LOADER, WebSphereEnvironmentProvider.class.getClassLoader());
        env.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, "com.github.veithen.visualwas.jmx");
        if (username != null) {
            env.put(JMXConnector.CREDENTIALS, new String[] { username, password });
        }
        return env;
    }
    
    @Override
    public void saveEnvironment(Storage storage) {
        if (persistent) {
            storage.setCustomProperty(PROPERTY_USERNAME, username);
            storage.setCustomProperty(PROPERTY_PASSWORD, Utils.encodePassword(password));
        }
    }
    
    @Override
    public void loadEnvironment(Storage storage) {
        username = storage.getCustomProperty(PROPERTY_USERNAME);
        String encodedPassword = storage.getCustomProperty(PROPERTY_PASSWORD);
        password = encodedPassword == null ? null : Utils.decodePassword(password);
        persistent = username != null;
    }
}
