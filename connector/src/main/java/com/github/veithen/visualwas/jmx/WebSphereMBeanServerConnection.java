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
package com.github.veithen.visualwas.jmx;

import java.io.IOException;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import com.github.veithen.visualwas.jmx.soap.SOAPJMXConnector;

/**
 * {@link MBeanServerConnection} extension interface that gives access to WebSphere specific
 * methods. This interface is implemented by all MBean server connections returned by
 * {@link SOAPJMXConnector}.
 */
public interface WebSphereMBeanServerConnection extends MBeanServerConnection {
    /**
     * Get the object name of the MBean representing the WebSphere Application Server instance the
     * client is connected to.
     * 
     * @return the object name of the MBean representing the server
     * @throws IOException
     */
    ObjectName getServerMBean() throws IOException;
}
