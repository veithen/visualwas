/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 Andreas Veithen
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
import org.apache.axiom.soap.SOAPBody;

public class OperationHandler {
    private static final QName XSI_NIL = new QName("http://www.w3.org/2001/XMLSchema-instance", "nil", "xsi");
    
    private final String operationName;
    private final String requestElementName;
    private final String responseElementName;
    private final ParamHandler[] paramHandlers;
    private final TypeHandler returnValueHandler;
    
    public OperationHandler(String operationName, String requestElementName, String responseElementName, ParamHandler[] paramHandlers, TypeHandler returnValueHandler) {
        this.operationName = operationName;
        this.requestElementName = requestElementName;
        this.responseElementName = responseElementName;
        this.paramHandlers = paramHandlers;
        this.returnValueHandler = returnValueHandler;
    }

    public void createRequest(SOAPBody body, Object[] args, InvocationContextImpl context) {
        OMFactory factory = body.getOMFactory();
        OMNamespace ns = factory.createOMNamespace("urn:AdminService", "ns");
        OMElement element = factory.createOMElement(requestElementName, ns, body);
        element.addAttribute("encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/", body.getNamespace());
        int paramCount = paramHandlers.length;
        if (paramCount > 0) {
            OMNamespace xsiNS = element.declareNamespace("http://www.w3.org/2001/XMLSchema-instance", "xsi");
            for (int i=0; i<paramCount; i++) {
                paramHandlers[i].createOMElement(element, xsiNS, args[i], context);
            }
        }
    }
    
    public Object processResponse(OMElement response, InvocationContextImpl context) throws ClassNotFoundException, OperationHandlerException {
        // TODO: check element names
        // TODO: check xsi:type???
        if (returnValueHandler == null) {
            // TODO: check that the body is empty
            return null;
        } else {
            OMElement returnElement = response.getFirstElement();
            if ("true".equals(returnElement.getAttributeValue(XSI_NIL))) {
                return null;
            } else {
                try {
                    return returnValueHandler.extractValue(returnElement, context);
                } catch (TypeHandlerException ex) {
                    throw new OperationHandlerException("Failed to extract return value for operation " + operationName, ex);
                }
            }
        }
    }
}
