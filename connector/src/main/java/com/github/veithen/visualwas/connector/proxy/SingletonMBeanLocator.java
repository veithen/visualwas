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
package com.github.veithen.visualwas.connector.proxy;

import java.io.IOException;
import java.util.Iterator;

import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.github.veithen.visualwas.connector.AdminService;

public class SingletonMBeanLocator implements MBeanLocator {
    private final String type;

    public SingletonMBeanLocator(String type) {
        this.type = type;
    }

    @Override
    public ObjectName locateMBean(AdminService adminService) throws IOException, InstanceNotFoundException {
        ObjectName serverMBean = adminService.getServerMBean();
        ObjectName pattern;
        try {
            pattern = new ObjectName(serverMBean.getDomain() + ":type=" + type + ",cell=" + serverMBean.getKeyProperty("cell") + ",node=" + serverMBean.getKeyProperty("node") + ",process=" + serverMBean.getKeyProperty("process") + ",*");
        } catch (MalformedObjectNameException ex) {
            // We should never get here
            throw new RuntimeException(ex);
        }
        Iterator<ObjectName> it = adminService.queryNames(pattern, null).iterator();
        if (it.hasNext()) {
            ObjectName mbean = it.next();
            if (it.hasNext()) {
                throw new InstanceNotFoundException("Found multiple MBeans of type " + type);
            } else {
                return mbean;
            }
        } else {
            throw new InstanceNotFoundException("No MBean of type " + type + " found");
        }
    }
}
