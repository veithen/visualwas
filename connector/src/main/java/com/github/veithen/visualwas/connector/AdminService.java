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
package com.github.veithen.visualwas.connector;

import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanInfo;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.QueryExp;

import com.github.veithen.visualwas.connector.description.AdminServiceDescription;
import com.github.veithen.visualwas.connector.description.AdminServiceDescriptionFactory;
import com.google.common.util.concurrent.ListenableFuture;

public interface AdminService {
    AdminServiceDescription DESCRIPTION = AdminServiceDescriptionFactory.getInstance().createDescription(AdminService.class);
    
    ListenableFuture<String> getDefaultDomain();
    
    /**
     * Get the object name of the MBean representing the WebSphere Application Server instance the
     * client is connected to.
     * 
     * @return the object name of the MBean representing the server
     */
    ListenableFuture<ObjectName> getServerMBean();
    
    ListenableFuture<Integer> getMBeanCount();
    
    ListenableFuture<Set<ObjectName>> queryNames(
            @Param(name="objectname") ObjectName objectName,
            @Param(name="queryexp") QueryExp queryExp);
    
    ListenableFuture<Set<ObjectInstance>> queryMBeans(
            @Param(name="objectname") ObjectName objectName,
            @Param(name="queryexp") QueryExp queryExp);

    ListenableFuture<Boolean> isRegistered(@Param(name="objectname") ObjectName objectName);
    
    ListenableFuture<MBeanInfo> getMBeanInfo(@Param(name="objectname") ObjectName objectName);
    
    ListenableFuture<Boolean> isInstanceOf(
            @Param(name="objectname") ObjectName objectName,
            @Param(name="classname") String className);
    
    /**
     * Invokes an operation on an MBean.
     * 
     * @param objectName
     *            the object name of the MBean on which the method is to be invoked
     * @param operationName
     *            the the name of the operation to be invoked
     * @param params
     *            the parameters to pass to the operation
     * @param signature
     *            the signature of the operation
     * @return the object returned by the operation
     */
    ListenableFuture<Object> invoke(
            @Param(name="objectname") ObjectName objectName,
            @Param(name="operationname") String operationName,
            @Param(name="params") Object[] params,
            @Param(name="signature") String[] signature);
    
    /**
     * Gets the value of a specific attribute of a named MBean.
     * 
     * @param objectName
     *            the object name of the MBean from which the attribute is to be retrieved
     * @param attribute
     *            the name of the attribute to be retrieved
     * @return the value of the retrieved attribute
     */
    ListenableFuture<Object> getAttribute(
            @Param(name="objectname") ObjectName objectName,
            @Param(name="attribute") String attribute);
    
    /**
     * Retrieves the values of several attributes of a named MBean.
     * 
     * @param objectName
     *            the object name of the MBean from which the attributes are retrieved
     * @param attributes
     *            a list of the attributes to be retrieved
     * @return the list of the retrieved attributes
     */
    ListenableFuture<AttributeList> getAttributes(
            @Param(name="objectname") ObjectName objectName,
            @Param(name="attribute") String[] attributes);

    ListenableFuture<Void> setAttribute(
            @Param(name="objectname") ObjectName objectName,
            @Param(name="attribute") Attribute attribute);
    
    ListenableFuture<AttributeList> setAttributes(
            @Param(name="objectname") ObjectName objectName,
            @Param(name="attribute") AttributeList attributes);
}
