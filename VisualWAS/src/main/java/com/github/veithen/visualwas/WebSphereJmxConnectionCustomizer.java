package com.github.veithen.visualwas;

import java.util.Set;

import com.github.veithen.visualwas.env.CustomWebSphereEnvironmentProvider;
import com.sun.tools.visualvm.application.Application;
import com.sun.tools.visualvm.core.datasource.DataSource;
import com.sun.tools.visualvm.core.explorer.ExplorerSupport;
import com.sun.tools.visualvm.core.properties.PropertiesPanel;
import com.sun.tools.visualvm.host.Host;
import com.sun.tools.visualvm.jmx.JmxConnectionCustomizer;

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
