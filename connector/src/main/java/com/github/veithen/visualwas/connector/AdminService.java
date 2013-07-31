package com.github.veithen.visualwas.connector;

import java.util.Set;

import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.ReflectionException;

public interface AdminService {
    String getDefaultDomain() throws ConnectorException;
    
    ObjectName getServerMBean() throws ConnectorException;
    
    Set<ObjectName> queryNames(@Param(name="objectname") ObjectName objectName,
                               @Param(name="queryexp") QueryExp queryExp) throws ConnectorException;

    boolean isRegistered(@Param(name="objectname") ObjectName objectName) throws ConnectorException;
    
    MBeanInfo getMBeanInfo(@Param(name="objectname") ObjectName objectName) throws InstanceNotFoundException, IntrospectionException, ReflectionException, ConnectorException;
    
    boolean isInstanceOf(@Param(name="objectname") ObjectName objectName,
                         @Param(name="classname") String className) throws InstanceNotFoundException, ConnectorException;
    
    Object invoke(@Param(name="objectname") ObjectName objectName,
                  @Param(name="operationname") String operationName,
                  @Param(name="params") Object[] params,
                  @Param(name="signature") String[] signature) throws InstanceNotFoundException, MBeanException, ReflectionException, ConnectorException;
    
    AttributeList getAttributes(@Param(name="objectname") ObjectName objectName,
                                @Param(name="attribute") String[] attributes) throws InstanceNotFoundException, ReflectionException, ConnectorException;
}
