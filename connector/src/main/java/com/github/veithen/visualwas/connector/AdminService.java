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
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.ReflectionException;

import com.github.veithen.visualwas.connector.description.AdminServiceDescription;
import com.github.veithen.visualwas.connector.description.AdminServiceDescriptionFactory;
import com.github.veithen.visualwas.connector.loader.ClassLoaderProvider;

public interface AdminService {
    AdminServiceDescription DESCRIPTION = AdminServiceDescriptionFactory.getInstance().createDescription(AdminService.class);
    
    String getDefaultDomain() throws IOException;
    
    ObjectName getServerMBean() throws IOException;
    
    Set<ObjectName> queryNames(@Param(name="objectname") ObjectName objectName,
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
}
