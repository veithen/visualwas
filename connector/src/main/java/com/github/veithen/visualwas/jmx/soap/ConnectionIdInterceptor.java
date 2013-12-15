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
package com.github.veithen.visualwas.jmx.soap;

import javax.xml.namespace.QName;

import org.apache.axiom.soap.SOAPEnvelope;

import com.github.veithen.visualwas.connector.feature.Interceptor;
import com.github.veithen.visualwas.connector.feature.InvocationContext;

final class ConnectionIdInterceptor implements Interceptor {
    private static final QName HEADER_NAME = new QName("http://github.com/veithen/visualwas", "ConnectionId", "v");
    
    private final String connectionId;

    ConnectionIdInterceptor(String connectionId) {
        this.connectionId = connectionId;
    }

    @Override
    public void processRequest(SOAPEnvelope request, InvocationContext context) {
        request.getOrCreateHeader().addHeaderBlock(HEADER_NAME).setText(connectionId);
    }
}
