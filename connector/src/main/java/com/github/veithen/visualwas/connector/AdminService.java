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
package com.github.veithen.visualwas.connector;

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

import com.github.veithen.visualwas.connector.description.AdminServiceDescription;
import com.github.veithen.visualwas.connector.description.AdminServiceDescriptionFactory;
import com.github.veithen.visualwas.connector.loader.ClassLoaderProvider;

public interface AdminService {
    AdminServiceDescription DESCRIPTION = AdminServiceDescriptionFactory.getInstance().createDescription(AdminService.class);
    
    String getDefaultDomain() throws IOException;
    
    /**
     * Get the object name of the MBean representing the WebSphere Application Server instance the
     * client is connected to.
     * 
     * @return the object name of the MBean representing the server
     * @throws IOException
     */
    ObjectName getServerMBean() throws IOException;
    
    Integer getMBeanCount() throws IOException;
    
    Set<ObjectName> queryNames(@Param(name="objectname") ObjectName objectName,
                               @Param(name="queryexp") QueryExp queryExp) throws IOException;
    
    Set<ObjectInstance> queryMBeans(@Param(name="objectname") ObjectName objectName,
                                    @Param(name="queryexp") QueryExp queryExp) throws IOException;

    boolean isRegistered(@Param(name="objectname") ObjectName objectName) throws IOException;
    
    MBeanInfo getMBeanInfo(@Param(name="objectname") ObjectName objectName) throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException;
    
    boolean isInstanceOf(@Param(name="objectname") ObjectName objectName,
                         @Param(name="classname") String className) throws InstanceNotFoundException, IOException;
    
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
     * @throws InstanceNotFoundException
     *             if no MBean with the given object name is registered in the MBean server
     * @throws MBeanException
     *             if the method on the MBean throws an exception (in which case the
     *             {@link MBeanException} wraps that exception)
     * @throws ReflectionException
     *             if a problem occurs while trying to invoke the method through reflection
     * @throws IOException
     *             if a communication problem occurred
     * @throws ClassNotFoundException
     *             if the result (return value or exception thrown by the operation) could not be
     *             deserialized because the class loader provided by the {@link ClassLoaderProvider}
     *             was unable to load a required class
     */
    Object invoke(@Param(name="objectname") ObjectName objectName,
                  @Param(name="operationname") String operationName,
                  @Param(name="params") Object[] params,
                  @Param(name="signature") String[] signature) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException, ClassNotFoundException;
    
    /**
     * Gets the value of a specific attribute of a named MBean.
     * 
     * @param objectName
     *            the object name of the MBean from which the attribute is to be retrieved
     * @param attribute
     *            the name of the attribute to be retrieved
     * @return the value of the retrieved attribute
     * @throws MBeanException
     *             if the MBean's getter method throws an exception (in which case the
     *             {@link MBeanException} wraps that exception)
     * @throws AttributeNotFoundException
     *             if the attribute specified is not accessible in the MBean
     * @throws InstanceNotFoundException
     *             if no MBean with the given object name is registered in the MBean server
     * @throws ReflectionException
     *             if a problem occurs while trying to invoke the getter method through reflection
     * @throws IOException
     *             if a communication problem occurred
     * @throws ClassNotFoundException
     *             if the attribute value (or the exception thrown by the getter method) could not
     *             be deserialized because the class loader provided by the
     *             {@link ClassLoaderProvider} was unable to load a required class
     */
    Object getAttribute(@Param(name="objectname") ObjectName objectName,
                        @Param(name="attribute") String attribute) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException, ClassNotFoundException;
    
    /**
     * Retrieves the values of several attributes of a named MBean.
     * 
     * @param objectName
     *            the object name of the MBean from which the attributes are retrieved
     * @param attributes
     *            a list of the attributes to be retrieved
     * @return the list of the retrieved attributes
     * @throws InstanceNotFoundException
     *             if no MBean with the given object name is registered in the MBean server
     * @throws ReflectionException
     *             if an exception occurred when trying to invoke the getAttributes method of a
     *             Dynamic MBean
     * @throws IOException
     *             if a communication problem occurred
     * @throws ClassNotFoundException
     *             if some attribute value could not be deserialized because the class loader
     *             provided by the {@link ClassLoaderProvider} was unable to load a required class
     */
    AttributeList getAttributes(@Param(name="objectname") ObjectName objectName,
                                @Param(name="attribute") String[] attributes) throws InstanceNotFoundException, ReflectionException, IOException, ClassNotFoundException;

    void setAttribute(@Param(name="objectname") ObjectName objectName,
                      @Param(name="attribute") Attribute attribute) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, IOException;
    
    AttributeList setAttributes(@Param(name="objectname") ObjectName objectName,
                                @Param(name="attribute") AttributeList attributes) throws InstanceNotFoundException, ReflectionException, IOException;
}
