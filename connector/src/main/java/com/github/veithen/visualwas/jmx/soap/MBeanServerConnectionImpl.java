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
package com.github.veithen.visualwas.jmx.soap;

import java.io.IOException;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistrationException;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.ReflectionException;

import com.github.veithen.visualwas.connector.AdminService;
import com.github.veithen.visualwas.connector.notification.NotificationDispatcher;
import com.github.veithen.visualwas.jmx.WebSphereMBeanServerConnection;

final class MBeanServerConnectionImpl implements WebSphereMBeanServerConnection {
    private final AdminService adminService;
    private final NotificationDispatcher notificationDispatcher;
    private final ExceptionTransformer exceptionTransformer;
    
    MBeanServerConnectionImpl(AdminService adminService, NotificationDispatcher notificationDispatcher, ExceptionTransformer exceptionTransformer) {
        this.adminService = adminService;
        this.notificationDispatcher = notificationDispatcher;
        this.exceptionTransformer = exceptionTransformer;
    }
    
    @Override
    public ObjectInstance createMBean(String className, ObjectName name) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public ObjectInstance createMBean(String className, ObjectName name, ObjectName loaderName) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public ObjectInstance createMBean(String className, ObjectName name, Object[] params, String[] signature) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public ObjectInstance createMBean(String className, ObjectName name, ObjectName loaderName, Object[] params, String[] signature) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void unregisterMBean(ObjectName name) throws InstanceNotFoundException, MBeanRegistrationException, IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public ObjectInstance getObjectInstance(ObjectName name) throws InstanceNotFoundException, IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<ObjectInstance> queryMBeans(ObjectName name, QueryExp query) throws IOException {
        return adminService.queryMBeans(name, query);
    }

    @Override
    public Set<ObjectName> queryNames(ObjectName name, QueryExp query) throws IOException {
        return adminService.queryNames(name, query);
    }

    @Override
    public boolean isRegistered(ObjectName name) throws IOException {
        return adminService.isRegistered(name);
    }

    @Override
    public Integer getMBeanCount() throws IOException {
        return adminService.getMBeanCount();
    }

    @Override
    public Object getAttribute(ObjectName name, String attribute) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException {
        try {
            return adminService.getAttribute(name, attribute);
        } catch (ClassNotFoundException ex) {
            throw exceptionTransformer.transform(ex);
        }
    }

    @Override
    public AttributeList getAttributes(ObjectName name, String[] attributes) throws InstanceNotFoundException, ReflectionException, IOException {
        try {
            return adminService.getAttributes(name, attributes);
        } catch (ClassNotFoundException ex) {
            throw exceptionTransformer.transform(ex);
        }
    }

    @Override
    public void setAttribute(ObjectName name, Attribute attribute) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, IOException {
        adminService.setAttribute(name, attribute);
    }

    @Override
    public AttributeList setAttributes(ObjectName name, AttributeList attributes) throws InstanceNotFoundException, ReflectionException, IOException {
        return adminService.setAttributes(name, attributes);
    }

    @Override
    public Object invoke(ObjectName name, String operationName, Object[] params, String[] signature) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException {
        try {
            return adminService.invoke(name, operationName, params, signature);
        } catch (ClassNotFoundException ex) {
            throw exceptionTransformer.transform(ex);
        }
    }

    @Override
    public String getDefaultDomain() throws IOException {
        return adminService.getDefaultDomain();
    }

    @Override
    public String[] getDomains() throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void addNotificationListener(ObjectName name, NotificationListener listener, NotificationFilter filter, Object handback) throws InstanceNotFoundException, IOException {
        if (isRegistered(name)) {
            notificationDispatcher.addNotificationListener(name, listener, filter, handback);
        } else {
            throw new InstanceNotFoundException(name + " not found");
        }
    }

    @Override
    public void addNotificationListener(ObjectName name, ObjectName listener, NotificationFilter filter, Object handback) throws InstanceNotFoundException, IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeNotificationListener(ObjectName name, ObjectName listener) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeNotificationListener(ObjectName name, ObjectName listener, NotificationFilter filter, Object handback) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeNotificationListener(ObjectName name, NotificationListener listener) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeNotificationListener(ObjectName name, NotificationListener listener, NotificationFilter filter, Object handback) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
        notificationDispatcher.removeNotificationListener(name, listener, filter, handback);
    }

    @Override
    public MBeanInfo getMBeanInfo(ObjectName name) throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException {
        return adminService.getMBeanInfo(name);
    }

    @Override
    public boolean isInstanceOf(ObjectName name, String className) throws InstanceNotFoundException, IOException {
        return adminService.isInstanceOf(name, className);
    }

    @Override
    public ObjectName getServerMBean() throws IOException {
        return adminService.getServerMBean();
    }
}
