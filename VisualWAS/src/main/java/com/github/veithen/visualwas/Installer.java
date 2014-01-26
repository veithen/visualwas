/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2014 Andreas Veithen
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
