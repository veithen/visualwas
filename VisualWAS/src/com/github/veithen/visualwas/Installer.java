/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.veithen.visualwas;

import com.sun.tools.visualvm.jmx.JmxConnectionSupport;
import org.openide.modules.ModuleInstall;

public class Installer extends ModuleInstall {
    @Override
    public void restored() {
        JmxConnectionSupport.getInstance().registerCustomizer(new WebSphereJmxConnectionCustomizer());
    }
}
