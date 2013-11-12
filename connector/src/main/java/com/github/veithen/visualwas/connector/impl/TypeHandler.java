package com.github.veithen.visualwas.connector.impl;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;

public interface TypeHandler {
    QName setValue(OMElement element, Object value, InvocationContextImpl context);
    
    /**
     * Extract the value from a given element.
     * 
     * @param element
     *            the element to extract the value from
     * @param context
     *            the invocation context
     * @return the value
     * @throws ClassNotFoundException
     *             if a required class could not be loaded by the class loader returned by
     *             {@link InvocationContextImpl#getClassLoader()}
     * @throws TypeHandlerException
     *             if another error occurred
     */
    Object extractValue(OMElement element, InvocationContextImpl context) throws ClassNotFoundException, TypeHandlerException;
}
