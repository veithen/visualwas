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
package com.github.veithen.visualwas.connector.transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPEnvelope;

import com.github.veithen.visualwas.connector.Callback;
import com.github.veithen.visualwas.connector.Handler;
import com.github.veithen.visualwas.connector.feature.InvocationContext;

final class DefaultTransport implements Handler<SOAPEnvelope,SOAPEnvelope,SOAPEnvelope> {
    private final URL endpointUrl;
    private final TransportConfiguration config;
    
    DefaultTransport(Endpoint endpoint, TransportConfiguration config) {
        this.endpointUrl = endpoint.createURL();
        this.config = config;
    }

    @Override
    public void invoke(InvocationContext context, SOAPEnvelope request, Callback<SOAPEnvelope,SOAPEnvelope> callback) {
        try {
            HttpURLConnection conn = config.createURLConnection(endpointUrl);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
            conn.connect();
            OutputStream out = conn.getOutputStream();
            OMOutputFormat format = new OMOutputFormat();
            format.setCharSetEncoding("UTF-8");
            // TODO: this should actually throw IOException
            request.serialize(out);
            out.close();
            boolean isOK = conn.getResponseCode() == 200;
            InputStream in = isOK ? conn.getInputStream() : conn.getErrorStream();
            try {
                SOAPEnvelope response = OMXMLBuilderFactory.createSOAPModelBuilder(in, "UTF-8").getSOAPEnvelope(); // TODO: encoding!
                if (isOK) {
                    callback.onResponse(response);
                } else {
                    callback.onFault(response);
                }
            } finally {
                in.close();
            }
        } catch (XMLStreamException ex) {
            // TODO: attempt to unwrap the exception first
            callback.onTransportError(new IOException(ex));
        } catch (IOException ex) {
            callback.onTransportError(ex);
        }
    }
}
