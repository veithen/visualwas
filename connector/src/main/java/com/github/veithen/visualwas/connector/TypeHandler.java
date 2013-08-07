package com.github.veithen.visualwas.connector;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;

public interface TypeHandler {
    QName setValue(OMElement element, Object value);
    Object extractValue(OMElement element) throws TypeHandlerException;
}
