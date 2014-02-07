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

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;

public abstract class SimpleTypeHandler implements TypeHandler {
    private static final Map<Class<?>,SimpleTypeHandler> handlerByJavaType = new HashMap<Class<?>,SimpleTypeHandler>();
    private static final Map<QName,SimpleTypeHandler> handlerBySchemaType = new HashMap<QName,SimpleTypeHandler>();

    static {
        register(String.class, new SimpleTypeHandler("string") {
            @Override
            protected String toString(Object value) {
                return (String)value;
            }
            
            @Override
            protected Object fromString(String s) {
                return s;
            }
        });
    }
    
    private final QName type;

    public SimpleTypeHandler(String type) {
        this.type = new QName("http://www.w3.org/2001/XMLSchema", type, "xsd");
    }

    private static void register(Class<?> javaType, SimpleTypeHandler handler) {
        handlerByJavaType.put(javaType, handler);
        handlerBySchemaType.put(handler.getType(), handler);
    }
    
    public static SimpleTypeHandler getByJavaType(Class<?> javaType) {
        return handlerByJavaType.get(javaType);
    }
    
    public static SimpleTypeHandler getBySchemaType(QName type) {
        return handlerBySchemaType.get(type);
    }
    
    public final QName getType() {
        return type;
    }

    @Override
    public final QName getXMLType(InvocationContextImpl context) {
        return type;
    }

    @Override
    public final QName setValue(OMElement element, Object value, InvocationContextImpl context) {
        element.setText(toString(value));
        return type;
    }

    @Override
    public final Object extractValue(OMElement element, InvocationContextImpl context) {
        return fromString(element.getText());
    }
    
    protected abstract String toString(Object value);
    protected abstract Object fromString(String s);
}
