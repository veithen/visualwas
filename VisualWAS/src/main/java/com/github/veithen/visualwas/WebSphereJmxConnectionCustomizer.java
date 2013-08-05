/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.veithen.visualwas;

import com.sun.tools.visualvm.application.Application;
import com.sun.tools.visualvm.core.properties.PropertiesPanel;
import com.sun.tools.visualvm.jmx.JmxConnectionCustomizer;
import org.openide.util.NbBundle;

/**
 *
 * @author veithen
 */
public class WebSphereJmxConnectionCustomizer extends JmxConnectionCustomizer {
    public WebSphereJmxConnectionCustomizer() {
        super(NbBundle.getMessage(WebSphereJmxConnectionCustomizer.class, "LBL_jmx_connection_name"),
              NbBundle.getMessage(WebSphereJmxConnectionCustomizer.class, "LBL_jmx_connection_descr"),
              2, false);
    }
    
    @Override
    public PropertiesPanel createPanel(Application application) {
        return new WebSpherePropertiesPanel();
    }

    @Override
    public Setup getConnectionSetup(PropertiesPanel panel) {
        WebSpherePropertiesPanel wasPanel = (WebSpherePropertiesPanel)panel;
        String hostPort = wasPanel.getHost() + ":" + wasPanel.getPort();
        return new Setup("service:jmx:soap://" + hostPort, hostPort,
                new WebSphereEnvironmentProvider(wasPanel.getUsername(), wasPanel.getPassword(), wasPanel.isSaveCredentials()), true);
    }
}
