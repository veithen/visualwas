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
package com.github.veithen.visualwas.connector.federation;

import com.github.veithen.visualwas.connector.feature.Configurator;
import com.github.veithen.visualwas.connector.feature.Feature;

/**
 * 
 * <h2>Non routable MBeans</h2> Normally all MBeans registered in WebSphere's MBean server are
 * routable, i.e. they have the <tt>cell</tt>, <tt>node</tt> and <tt>process</tt> key properties and
 * can be invoked through an upstream administrative agent (deployment manager or node agent). This
 * applies even to custom MBeans registered by applications deployed on WebSphere. There are however
 * a few exceptions:
 * <ul>
 * <li>There is always an MBean with object name <tt>JMImplementation:type=MBeanServerDelegate</tt>
 * which is not routable.
 * <li>If the <tt>javax.management.builder.initial</tt> system property is overridden (using a
 * generic JVM argument in the WebSphere configuration), then custom MBeans are not routable. Note
 * that tampering with the <tt>javax.management.builder.initial</tt> system property is not
 * supported by IBM and you shouldn't do that (at least not on production systems).
 * <li>WebSphere's MBean server is of type <tt>com.ibm.ws.management.PlatformMBeanServer</tt>
 * (unless the <tt>javax.management.builder.initial</tt> system property has been overridden). This
 * class is actually a wrapper around a standard MBean server instance and the
 * <tt>getDefaultMBeanServer</tt> method can be used to get a reference to the underlying MBean
 * server. MBeans registered on that MBean server will not be routable. This feature is used by
 * XM4WAS to register the platform MXBeans in WebSphere's MBean server. These MBeans belong to the
 * <tt>java.lang</tt> domain.
 * </ul>
 * The disable federation feature makes the simplifying assumption that all MBeans are routable,
 * except for MBeans in a certain set of domains. Currently, the set of domains is hardcoded to
 * <tt>JMImplementation</tt> and <tt>java.lang</tt> to cover the first and last cases identified
 * above. The second case is thus not covered.
 */
public final class DisableFederationFeature implements Feature {
    public static final DisableFederationFeature INSTANCE = new DisableFederationFeature();

    private DisableFederationFeature() {}

    @Override
    public void configureConnector(Configurator configurator) {
        configurator.addInvocationInterceptor(new InvocationInterceptor());
    }
}
