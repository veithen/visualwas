package com.github.veithen.visualwas.connector.impl;

import java.io.IOException;
import java.util.Set;

import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.ReflectionException;

import com.github.veithen.visualwas.connector.AdminService;
import com.github.veithen.visualwas.connector.Connector;

final class ConnectorImpl implements Connector {
    private final AdminService adminService;
    private final AdaptableDelegate adaptableDelegate;
    
    ConnectorImpl(AdminService adminService, AdaptableDelegate adaptableDelegate) {
        this.adminService = adminService;
        this.adaptableDelegate = adaptableDelegate;
        adaptableDelegate.setAdminService(adminService);
    }
    
    public String getDefaultDomain() throws IOException {
        return adminService.getDefaultDomain();
    }

    public ObjectName getServerMBean() throws IOException {
        return adminService.getServerMBean();
    }

    public Set<ObjectName> queryNames(ObjectName objectName, QueryExp queryExp) throws IOException {
        return adminService.queryNames(objectName, queryExp);
    }

    public boolean isRegistered(ObjectName objectName) throws IOException {
        return adminService.isRegistered(objectName);
    }

    public MBeanInfo getMBeanInfo(ObjectName objectName) throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException {
        return adminService.getMBeanInfo(objectName);
    }

    public boolean isInstanceOf(ObjectName objectName, String className) throws InstanceNotFoundException, IOException {
        return adminService.isInstanceOf(objectName, className);
    }

    public Object invoke(ObjectName objectName, String operationName, Object[] params, String[] signature) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException {
        return adminService.invoke(objectName, operationName, params, signature);
    }

    public Object getAttribute(ObjectName objectName, String attribute) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException {
        return adminService.getAttribute(objectName, attribute);
    }

    public AttributeList getAttributes(ObjectName objectName, String[] attributes) throws InstanceNotFoundException, ReflectionException, IOException {
        return adminService.getAttributes(objectName, attributes);
    }

    public <T> T getAdapter(Class<T> clazz) {
        return adaptableDelegate.getAdapter(clazz);
    }
}
