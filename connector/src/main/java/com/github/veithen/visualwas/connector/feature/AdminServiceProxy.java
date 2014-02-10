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
package com.github.veithen.visualwas.connector.feature;

import java.io.IOException;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.ReflectionException;

import com.github.veithen.visualwas.connector.AdminService;

public class AdminServiceProxy implements AdminService {
    private final AdminService target;

    public AdminServiceProxy(AdminService target) {
        this.target = target;
    }

    public String getDefaultDomain() throws IOException {
        return target.getDefaultDomain();
    }

    public ObjectName getServerMBean() throws IOException {
        return target.getServerMBean();
    }

    public Integer getMBeanCount() throws IOException {
        return target.getMBeanCount();
    }

    public Set<ObjectName> queryNames(ObjectName objectName, QueryExp queryExp)
            throws IOException {
        return target.queryNames(objectName, queryExp);
    }

    public Set<ObjectInstance> queryMBeans(ObjectName objectName,
            QueryExp queryExp) throws IOException {
        return target.queryMBeans(objectName, queryExp);
    }

    public boolean isRegistered(ObjectName objectName) throws IOException {
        return target.isRegistered(objectName);
    }

    public MBeanInfo getMBeanInfo(ObjectName objectName)
            throws InstanceNotFoundException, IntrospectionException,
            ReflectionException, IOException {
        return target.getMBeanInfo(objectName);
    }

    public boolean isInstanceOf(ObjectName objectName, String className)
            throws InstanceNotFoundException, IOException {
        return target.isInstanceOf(objectName, className);
    }

    public Object invoke(ObjectName objectName, String operationName,
            Object[] params, String[] signature)
            throws InstanceNotFoundException, MBeanException,
            ReflectionException, IOException, ClassNotFoundException {
        return target.invoke(objectName, operationName, params, signature);
    }

    public Object getAttribute(ObjectName objectName, String attribute)
            throws MBeanException, AttributeNotFoundException,
            InstanceNotFoundException, ReflectionException, IOException,
            ClassNotFoundException {
        return target.getAttribute(objectName, attribute);
    }

    public AttributeList getAttributes(ObjectName objectName,
            String[] attributes) throws InstanceNotFoundException,
            ReflectionException, IOException, ClassNotFoundException {
        return target.getAttributes(objectName, attributes);
    }

    public void setAttribute(ObjectName objectName, Attribute attribute)
            throws InstanceNotFoundException, AttributeNotFoundException,
            InvalidAttributeValueException, MBeanException,
            ReflectionException, IOException {
        target.setAttribute(objectName, attribute);
    }
}
