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
package com.github.veithen.visualwas.connector.impl;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;

public final class ParamHandler {
    private final String name;
    private final TypeHandler valueHandler;

    public ParamHandler(String name, TypeHandler valueHandler) {
        this.name = name;
        this.valueHandler = valueHandler;
    }
    
    public void createOMElement(OMElement operationElement, OMNamespace xsiNS, Object value, InvocationContextImpl context) {
        OMFactory factory = operationElement.getOMFactory();
        OMElement element = factory.createOMElement(name, null, operationElement);
        QName type = valueHandler.setValue(element, value, context);
        OMNamespace ns = element.findNamespace(type.getNamespaceURI(), null);
        if (ns == null) {
            ns = element.declareNamespace(type.getNamespaceURI(), type.getPrefix());
        }
        // TODO: parameter order of addAttribute is not consistent
        // TODO: there should be a method to add an attribute with a QName value
        element.addAttribute("type", ns.getPrefix() + ":" + type.getLocalPart(), xsiNS);
    }
}
