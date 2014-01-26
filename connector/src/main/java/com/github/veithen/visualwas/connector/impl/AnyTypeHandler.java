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

public class AnyTypeHandler implements TypeHandler {
    private static final QName XSI_TYPE = new QName("http://www.w3.org/2001/XMLSchema-instance", "type");
    
    private final ObjectHandler objectHandler;
    
    public AnyTypeHandler() {
        objectHandler = new ObjectHandler(Object.class);
    }
    
    @Override
    public QName setValue(OMElement element, Object value, InvocationContextImpl context) {
        SimpleTypeHandler simpleTypeHandler = SimpleTypeHandler.getByJavaType(value.getClass());
        if (simpleTypeHandler != null) {
            return simpleTypeHandler.setValue(element, value, context);
        } else {
            return objectHandler.setValue(element, value, context);
        }
    }

    @Override
    public Object extractValue(OMElement element, InvocationContextImpl context) throws ClassNotFoundException, TypeHandlerException {
        QName type = element.resolveQName(element.getAttributeValue(XSI_TYPE));
        SimpleTypeHandler simpleTypeHandler = SimpleTypeHandler.getBySchemaType(type);
        return (simpleTypeHandler != null ? simpleTypeHandler : objectHandler).extractValue(element, context);
    }
}
