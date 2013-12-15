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
package com.github.veithen.visualwas.connector.security;

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeader;

import com.github.veithen.visualwas.connector.feature.Interceptor;
import com.github.veithen.visualwas.connector.feature.InvocationContext;

final class SecurityInterceptor implements Interceptor {
    static final SecurityInterceptor INSTANCE = new SecurityInterceptor();
    
    private SecurityInterceptor() {}

    @Override
    public void processRequest(SOAPEnvelope request, InvocationContext context) {
        Credentials credentials = context.getAttribute(Credentials.class);
        if (credentials != null) {
            OMFactory factory = request.getOMFactory();
            SOAPHeader header = request.getOrCreateHeader();
            OMNamespace ns = factory.createOMNamespace("admin", "ns");
            header.addAttribute("SecurityEnabled", "true", ns);
            if (credentials instanceof BasicAuthCredentials) {
                BasicAuthCredentials basicAuthCreds = (BasicAuthCredentials)credentials;
                factory.createOMElement("username", null, header).setText(basicAuthCreds.getUsername());
                factory.createOMElement("password", null, header).setText(basicAuthCreds.getPassword());
                factory.createOMElement("LoginMethod", null, header).setText("BasicAuth");
            } else {
                // TODO: proper exception
                throw new UnsupportedOperationException();
            }
        }
    }
}
