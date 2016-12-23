/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2016 Andreas Veithen
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
package com.github.veithen.visualwas.client.config;

import com.github.veithen.visualwas.connector.feature.Configurator;
import com.github.veithen.visualwas.connector.feature.Dependencies;
import com.github.veithen.visualwas.connector.feature.Feature;
import com.github.veithen.visualwas.connector.mapped.ClassMappingConfigurator;
import com.github.veithen.visualwas.connector.mapped.ClassMappingFeature;
import com.github.veithen.visualwas.connector.proxy.MBeanProxyConfigurator;
import com.github.veithen.visualwas.connector.proxy.MBeanProxyFeature;
import com.github.veithen.visualwas.connector.proxy.SingletonMBeanLocator;
import com.github.veithen.visualwas.framework.proxy.Interface;
import com.github.veithen.visualwas.framework.proxy.InterfaceFactory;

@Dependencies({ClassMappingFeature.class, MBeanProxyFeature.class})
public class ConfigClientFeature implements Feature {
    private static final Interface<ConfigService> CONFIG_SERVICE_INTERFACE = InterfaceFactory.createInterface(ConfigService.class);
    
    public static final ConfigClientFeature INSTANCE = new ConfigClientFeature();
    
    private ConfigClientFeature() {}

    @Override
    public void configureConnector(Configurator configurator) {
        configurator.getAdapter(ClassMappingConfigurator.class).addMappedClasses(ConfigDataId.class);
        configurator.getAdapter(MBeanProxyConfigurator.class).registerProxy(CONFIG_SERVICE_INTERFACE, new SingletonMBeanLocator("ConfigService"));
    }
}
