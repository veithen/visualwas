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
package com.github.veithen.visualwas.connector.impl;

import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanInfo;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.QueryExp;

import com.github.veithen.visualwas.connector.AdminService;
import com.github.veithen.visualwas.connector.Connector;
import com.google.common.util.concurrent.ListenableFuture;

final class ConnectorImpl implements Connector {
    private final AdminService adminService;
    private final AdaptableDelegate adaptableDelegate;
    
    ConnectorImpl(AdminService adminService, AdaptableDelegate adaptableDelegate) {
        this.adaptableDelegate = adaptableDelegate;
        adaptableDelegate.setAdminService(adminService);
        this.adminService = adminService;
    }
    
    public ListenableFuture<String> getDefaultDomain() {
        return adminService.getDefaultDomain();
    }

    public ListenableFuture<ObjectName> getServerMBean() {
        return adminService.getServerMBean();
    }

    public ListenableFuture<Integer> getMBeanCount() {
        return adminService.getMBeanCount();
    }

    public ListenableFuture<Set<ObjectName>> queryNames(ObjectName objectName, QueryExp queryExp) {
        return adminService.queryNames(objectName, queryExp);
    }

    public ListenableFuture<Set<ObjectInstance>> queryMBeans(ObjectName objectName, QueryExp queryExp) {
        return adminService.queryMBeans(objectName, queryExp);
    }

    public ListenableFuture<Boolean> isRegistered(ObjectName objectName) {
        return adminService.isRegistered(objectName);
    }

    public ListenableFuture<MBeanInfo> getMBeanInfo(ObjectName objectName) {
        return adminService.getMBeanInfo(objectName);
    }

    public ListenableFuture<Boolean> isInstanceOf(ObjectName objectName, String className) {
        return adminService.isInstanceOf(objectName, className);
    }

    public ListenableFuture<Object> invoke(ObjectName objectName, String operationName, Object[] params, String[] signature) {
        return adminService.invoke(objectName, operationName, params, signature);
    }

    public ListenableFuture<Object> getAttribute(ObjectName objectName, String attribute) {
        return adminService.getAttribute(objectName, attribute);
    }

    public ListenableFuture<AttributeList> getAttributes(ObjectName objectName, String[] attributes) {
        return adminService.getAttributes(objectName, attributes);
    }

    public ListenableFuture<Void> setAttribute(ObjectName objectName, Attribute attribute) {
        return adminService.setAttribute(objectName, attribute);
    }

    public ListenableFuture<AttributeList> setAttributes(ObjectName objectName, AttributeList attributes) {
        return adminService.setAttributes(objectName, attributes);
    }

    public <T> T getAdapter(Class<T> clazz) {
        return adaptableDelegate.getAdapter(clazz);
    }

    public void close() {
        adaptableDelegate.closing();
        // TODO: ensure that the connector throws an exception when an attempt is made to use it after close has been called!
    }
}
