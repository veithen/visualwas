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

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;

public final class ObjectHandler implements TypeHandler {
    private final Class<?> type;
    
    public ObjectHandler(Class<?> type) {
        this.type = type;
    }

    @Override
    public QName getXMLType(InvocationContextImpl context) {
        return new QName("http://schemas.xmlsoap.org/soap/encoding/", context.getSerializer().getRemoteClassName(type), "soap");
    }

    @Override
    public QName setValue(OMElement element, Object value, InvocationContextImpl context) {
        element.addChild(element.getOMFactory().createOMText(new ObjectDataHandler(value, context), false));
        return new QName("urn:AdminService", context.getSerializer().getRemoteClassName(type));
    }

    @Override
    public Object extractValue(OMElement element, InvocationContextImpl context) throws ClassNotFoundException, TypeHandlerException {
        // TODO: suboptimal because it caches the data
        XMLStreamReader reader = element.getXMLStreamReader(false);
        try {
            reader.next();
            DataHandler dh = XMLStreamReaderUtils.getDataHandlerFromElement(reader);
            return context.getSerializer().readObject(dh.getInputStream(), context);
        } catch (ClassNotFoundException ex) {
            // Propagate the exception
            throw ex;
        } catch (Exception ex) {
            throw new TypeHandlerException("Failed to deserialize object (expected type: " + type.getName() + ")", ex);
        }
    }
}
