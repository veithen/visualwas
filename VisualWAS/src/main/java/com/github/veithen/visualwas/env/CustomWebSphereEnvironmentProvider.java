/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 Andreas Veithen
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
