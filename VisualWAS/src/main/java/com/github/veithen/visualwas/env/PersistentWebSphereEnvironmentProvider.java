package com.github.veithen.visualwas.env;

import java.util.Map;

import javax.management.remote.JMXConnector;

import com.github.veithen.visualwas.Installer;
import com.sun.tools.visualvm.application.Application;
import com.sun.tools.visualvm.core.datasource.Storage;
import com.sun.tools.visualvm.jmx.CredentialsProvider;
import com.sun.tools.visualvm.jmx.EnvironmentProvider;

/**
 * WebSphere {@link EnvironmentProvider} implementation used when loading the configuration from
 * persistent storage. It is registered by the {@link Installer}.
 */
public class PersistentWebSphereEnvironmentProvider extends CredentialsProvider.Persistent {
    @Override
    public String getId() {
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
