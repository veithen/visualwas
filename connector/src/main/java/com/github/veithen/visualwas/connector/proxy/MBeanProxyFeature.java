/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2018 Andreas Veithen
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
package com.github.veithen.visualwas.connector.proxy;

import com.github.veithen.visualwas.connector.Connector;
import com.github.veithen.visualwas.connector.feature.Configurator;
import com.github.veithen.visualwas.connector.feature.ConfiguratorAdapter;
import com.github.veithen.visualwas.connector.feature.Feature;
import com.github.veithen.visualwas.framework.proxy.Interface;

/**
 * Feature that enables the creation of MBean proxies. Proxies can be created in two different ways:
 * <ol>
 * <li>A feature can use {@link MBeanProxyConfigurator#registerProxy(Interface, MBeanLocator)} to register a
 * proxy. That proxy will be available through {@link Connector#getAdapter(Class)}. This will create
 * a single proxy instance per connection. This method is typically used for singleton MBeans.
 * <li>Application code can use {@link MBeanProxyFactory} (obtained using
 * {@link Connector#getAdapter(Class)} to create proxies. These proxies are not cached.
 * </ol>
 */
@ConfiguratorAdapter(MBeanProxyConfigurator.class)
public final class MBeanProxyFeature implements Feature {
    public static final MBeanProxyFeature INSTANCE = new MBeanProxyFeature();
    
    private MBeanProxyFeature() {}

    @Override
    public void configureConnector(Configurator configurator) {
        configurator.registerConfiguratorAdapter(MBeanProxyConfigurator.class, new MBeanProxyConfiguratorImpl(configurator));
        configurator.registerAdminServiceAdapter(MBeanProxyFactory.class, MBeanProxyFactoryImpl::new);
    }
}
