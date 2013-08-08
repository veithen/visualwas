/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.veithen.visualwas;

import org.openide.modules.ModuleInstall;

import com.github.veithen.visualwas.env.PersistentWebSphereEnvironmentProvider;
import com.sun.tools.visualvm.jmx.JmxConnectionSupport;

public class Installer extends ModuleInstall {
    @Override
    public void restored() {
        JmxConnectionSupport jmxConnectionSupport = JmxConnectionSupport.getInstance();
        jmxConnectionSupport.registerCustomizer(new WebSphereJmxConnectionCustomizer());
        jmxConnectionSupport.registerProvider(new PersistentWebSphereEnvironmentProvider());
    }
}
