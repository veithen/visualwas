package com.github.veithen.visualwas.connector;

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

public interface AdminService {
    String getDefaultDomain() throws IOException;
    
    ObjectName getServerMBean() throws IOException;
    
    Set<ObjectName> queryNames(@Param(name="objectname") ObjectName objectName,
                               @Param(name="queryexp") QueryExp queryExp) throws IOException;

    boolean isRegistered(@Param(name="objectname") ObjectName objectName) throws IOException;
    
    MBeanInfo getMBeanInfo(@Param(name="objectname") ObjectName objectName) throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException;
    
    boolean isInstanceOf(@Param(name="objectname") ObjectName objectName,
                         @Param(name="classname") String className) throws InstanceNotFoundException, IOException;
    
    Object invoke(@Param(name="objectname") ObjectName objectName,
                  @Param(name="operationname") String operationName,
                  @Param(name="params") Object[] params,
                  @Param(name="signature") String[] signature) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException;
    
    Object getAttribute(@Param(name="objectname") ObjectName objectName,
                        @Param(name="attribute") String attribute) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException;
    
    AttributeList getAttributes(@Param(name="objectname") ObjectName objectName,
                                @Param(name="attribute") String[] attributes) throws InstanceNotFoundException, ReflectionException, IOException;
}
