package com.github.veithen.visualwas.connector.impl;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;

public interface TypeHandler {
    QName setValue(OMElement element, Object value, InvocationContextImpl context);
    Object extractValue(OMElement element, InvocationContextImpl context) throws TypeHandlerException;
}
