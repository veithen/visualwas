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
package com.github.veithen.visualwas.connector.impl;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

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
import com.github.veithen.visualwas.connector.Connector;

final class ConnectorImpl implements Connector {
    private final AdminService adminService;
    private final AdaptableDelegate adaptableDelegate;
    
    ConnectorImpl(AdminService adminService, AdaptableDelegate adaptableDelegate) {
        this.adaptableDelegate = adaptableDelegate;
        adaptableDelegate.setAdminService(adminService);
        this.adminService = adminService;
    }
    
    public String getDefaultDomain() throws IOException {
        return adminService.getDefaultDomain();
    }

    public CompletableFuture<String> getDefaultDomainAsync() {
        return adminService.getDefaultDomainAsync();
    }

    public ObjectName getServerMBean() throws IOException {
        return adminService.getServerMBean();
    }

    public CompletableFuture<ObjectName> getServerMBeanAsync() {
        return adminService.getServerMBeanAsync();
    }

    public Integer getMBeanCount() throws IOException {
        return adminService.getMBeanCount();
    }

    public CompletableFuture<Integer> getMBeanCountAsync() {
        return adminService.getMBeanCountAsync();
    }

    public Set<ObjectName> queryNames(ObjectName objectName, QueryExp queryExp) throws IOException {
        return adminService.queryNames(objectName, queryExp);
    }

    public CompletableFuture<Set<ObjectName>> queryNamesAsync(ObjectName objectName, QueryExp queryExp) {
        return adminService.queryNamesAsync(objectName, queryExp);
    }

    public Set<ObjectInstance> queryMBeans(ObjectName objectName, QueryExp queryExp) throws IOException {
        return adminService.queryMBeans(objectName, queryExp);
    }

    public CompletableFuture<Set<ObjectInstance>> queryMBeansAsync(ObjectName objectName, QueryExp queryExp) {
        return adminService.queryMBeansAsync(objectName, queryExp);
    }

    public boolean isRegistered(ObjectName objectName) throws IOException {
        return adminService.isRegistered(objectName);
    }

    public CompletableFuture<Boolean> isRegisteredAsync(ObjectName objectName) {
        return adminService.isRegisteredAsync(objectName);
    }

    public MBeanInfo getMBeanInfo(ObjectName objectName) throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException {
        return adminService.getMBeanInfo(objectName);
    }

    public CompletableFuture<MBeanInfo> getMBeanInfoAsync(ObjectName objectName) {
        return adminService.getMBeanInfoAsync(objectName);
    }

    public boolean isInstanceOf(ObjectName objectName, String className) throws InstanceNotFoundException, IOException {
        return adminService.isInstanceOf(objectName, className);
    }

    public CompletableFuture<Boolean> isInstanceOfAsync(ObjectName objectName, String className) {
        return adminService.isInstanceOfAsync(objectName, className);
    }

    public Object invoke(ObjectName objectName, String operationName, Object[] params, String[] signature) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException, ClassNotFoundException {
        return adminService.invoke(objectName, operationName, params, signature);
    }

    public CompletableFuture<Object> invokeAsync(ObjectName objectName, String operationName, Object[] params, String[] signature) {
        return adminService.invokeAsync(objectName, operationName, params, signature);
    }

    public Object getAttribute(ObjectName objectName, String attribute) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException, ClassNotFoundException {
        return adminService.getAttribute(objectName, attribute);
    }

    public CompletableFuture<Object> getAttributeAsync(ObjectName objectName, String attribute) {
        return adminService.getAttributeAsync(objectName, attribute);
    }

    public AttributeList getAttributes(ObjectName objectName, String[] attributes) throws InstanceNotFoundException, ReflectionException, IOException, ClassNotFoundException {
        return adminService.getAttributes(objectName, attributes);
    }

    public CompletableFuture<AttributeList> getAttributesAsync(ObjectName objectName, String[] attributes) {
        return adminService.getAttributesAsync(objectName, attributes);
    }

    public void setAttribute(ObjectName objectName, Attribute attribute) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, IOException {
        adminService.setAttribute(objectName, attribute);
    }

    public CompletableFuture<Void> setAttributeAsync(ObjectName objectName, Attribute attribute) {
        return adminService.setAttributeAsync(objectName, attribute);
    }

    public AttributeList setAttributes(ObjectName objectName, AttributeList attributes) throws InstanceNotFoundException, ReflectionException, IOException {
        return adminService.setAttributes(objectName, attributes);
    }

    public CompletableFuture<AttributeList> setAttributesAsync(ObjectName objectName, AttributeList attributes) {
        return adminService.setAttributesAsync(objectName, attributes);
    }

    public <T> T getAdapter(Class<T> clazz) {
        return adaptableDelegate.getAdapter(clazz);
    }

    public void close() {
        adaptableDelegate.closing();
        // TODO: ensure that the connector throws an exception when an attempt is made to use it after close has been called!
    }
}
