package com.github.veithen.visualwas.env;

import java.util.Map;

import javax.management.remote.JMXConnector;

import com.sun.tools.visualvm.application.Application;
import com.sun.tools.visualvm.core.datasource.Storage;
import com.sun.tools.visualvm.jmx.CredentialsProvider;
import com.sun.tools.visualvm.jmx.EnvironmentProvider;

/**
 * WebSphere {@link EnvironmentProvider} implementation used during initial connection creation.
 * Configuration information is passed to the constructor.
 */
public class CustomWebSphereEnvironmentProvider extends CredentialsProvider.Custom {
    private final String username;
    private final char[] password;
    private final boolean persistent;
    
    public CustomWebSphereEnvironmentProvider(String username, char[] password, boolean persistent) {
        super(username, password, persistent);
        this.username = username;
        this.password = password;
        this.persistent = persistent;
    }

    public String getUsername() {
        return username;
    }

    public char[] getPassword() {
        return password;
    }

    public boolean isPersistent() {
        return persistent;
    }

    @Override
    public String getId() {
        // Return the ID of the persistent EnvironmentProvider so that the configuration can
        // be reloaded later (after a restart of VisualVM)
        return PersistentWebSphereEnvironmentProvider.class.getName();
    }

    @Override
    public Map<String,?> getEnvironment(Application application, Storage storage) {
        Map<String,?> parentEnv = super.getEnvironment(application, storage);
        Map<String,Object> env = EnvUtil.createEnvironment(parentEnv.get(JMXConnector.CREDENTIALS) != null);
        env.putAll(parentEnv);
        return env;
    }
}
