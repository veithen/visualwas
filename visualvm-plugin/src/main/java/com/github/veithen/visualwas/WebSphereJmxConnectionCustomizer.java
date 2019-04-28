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
package com.github.veithen.visualwas;

import java.util.Set;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;

import com.github.veithen.visualwas.env.CustomWebSphereEnvironmentProvider;
import com.sun.tools.visualvm.application.Application;
import com.sun.tools.visualvm.core.datasource.DataSource;
import com.sun.tools.visualvm.core.datasource.Storage;
import com.sun.tools.visualvm.core.explorer.ExplorerSupport;
import com.sun.tools.visualvm.core.properties.PropertiesPanel;
import com.sun.tools.visualvm.host.Host;
import com.sun.tools.visualvm.jmx.EnvironmentProvider;
import com.sun.tools.visualvm.jmx.JmxConnectionCustomizer;
import com.sun.tools.visualvm.jmx.impl.JmxApplication;
import com.sun.tools.visualvm.tools.jmx.JmxModelFactory;

import org.openide.util.NbBundle;

public class WebSphereJmxConnectionCustomizer extends JmxConnectionCustomizer {
    public WebSphereJmxConnectionCustomizer() {
        super(NbBundle.getMessage(WebSphereJmxConnectionCustomizer.class, "LBL_jmx_connection_name"),
              NbBundle.getMessage(WebSphereJmxConnectionCustomizer.class, "LBL_jmx_connection_descr"),
              2, false);
    }
    
    @Override
    public PropertiesPanel createPanel(Application application) {
        WebSpherePropertiesPanel panel = new WebSpherePropertiesPanel();
        if (application == null) {
            Set<DataSource> selectedDataSources = ExplorerSupport.sharedInstance().getSelectedDataSources();
            if (selectedDataSources.size() == 1) {
                DataSource selectedDataSource = selectedDataSources.iterator().next();
                if (selectedDataSource instanceof Host) {
                    panel.setHost(((Host)selectedDataSource).getHostName());
                }
            }
        } else {
            // Doesn't work because JmxApplication is not part of the public API
/*
            JmxApplication jmxApplication = (JmxApplication)application;
            JMXServiceURL url = jmxApplication.getJMXServiceURL();
            panel.setHost(url.getHost());
            panel.setPort(url.getPort());
            EnvironmentProvider envProvider = jmxApplication.getEnvironmentProvider();
            if (envProvider instanceof CustomWebSphereEnvironmentProvider) {
                CustomWebSphereEnvironmentProvider customEnvProvider = (CustomWebSphereEnvironmentProvider)envProvider;
                String username = customEnvProvider.getUsername();
                panel.setSecurityEnabled(username != null);
                panel.setUsername(username);
                panel.setPassword(customEnvProvider.getPassword());
                panel.setSaveCredentials(customEnvProvider.isPersistent());
            } else {
                String[] credentials = (String[])envProvider.getEnvironment(application, application.getStorage()).get(JMXConnector.CREDENTIALS);
                if (credentials == null) {
                    panel.setSecurityEnabled(false);
                } else {
                    panel.setSecurityEnabled(true);
                    panel.setUsername(credentials[0]);
                    String password = credentials[1];
                    if (password != null) {
                        panel.setPassword(password.toCharArray());
                        panel.setSaveCredentials(true);
                    }
                }
            }
*/
            JMXServiceURL url = JmxModelFactory.getJmxModelFor(application).getJMXServiceURL();
            panel.setHost(url.getHost());
            panel.setPort(url.getPort());
            Storage storage = application.getStorage();
            String username = storage.getCustomProperty("prop_credentials_username");
            String password = storage.getCustomProperty("prop_credentials_password");
            panel.setUsername(username);
            panel.setPassword(password);
            boolean security = username != null;
            panel.setSecurityEnabled(security);
            // TODO: not entirely correct
            panel.setSaveCredentials(security);
        }
        return panel;
    }

    @Override
    public Setup getConnectionSetup(PropertiesPanel panel) {
        WebSpherePropertiesPanel wasPanel = (WebSpherePropertiesPanel)panel;
        String hostPort = wasPanel.getHost() + ":" + wasPanel.getPort();
        return new Setup("service:jmx:soap://" + hostPort, hostPort,
                new CustomWebSphereEnvironmentProvider(wasPanel.getUsername(), wasPanel.getPassword(), wasPanel.isSaveCredentials()), true);
    }
}
