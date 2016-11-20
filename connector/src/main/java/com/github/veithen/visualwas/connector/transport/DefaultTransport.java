/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2016 Andreas Veithen
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
import java.util.concurrent.Callable;

import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPEnvelope;

import com.github.veithen.visualwas.connector.Handler;
import com.github.veithen.visualwas.connector.feature.InvocationContext;
import com.github.veithen.visualwas.connector.feature.SOAPResponse;
import com.google.common.util.concurrent.ListenableFuture;

final class DefaultTransport implements Handler<SOAPEnvelope,SOAPResponse> {
    private final URL endpointUrl;
    private final TransportConfiguration config;
    
    DefaultTransport(Endpoint endpoint, TransportConfiguration config) {
        this.endpointUrl = endpoint.createURL();
        this.config = config;
    }

    @Override
    public ListenableFuture<SOAPResponse> invoke(InvocationContext context, final SOAPEnvelope request) {
        return context.getExecutor().submit(new Callable<SOAPResponse>() {
            @Override
            public SOAPResponse call() throws Exception {
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
                final boolean isFault = conn.getResponseCode() != 200;
                final InputStream in = isFault ? conn.getErrorStream() : conn.getInputStream();
                try {
                    final SOAPEnvelope responseEnvelope = OMXMLBuilderFactory.createSOAPModelBuilder(in, "UTF-8").getSOAPEnvelope(); // TODO: encoding!
                    return new SOAPResponse() {
                        @Override
                        public boolean isFault() {
                            return isFault;
                        }
                        
                        @Override
                        public SOAPEnvelope getEnvelope() {
                            return responseEnvelope;
                        }
                        
                        @Override
                        public void discard() throws IOException {
                            in.close();
                        }
                    };
                } catch (Exception ex) {
                    try {
                        in.close();
                    } catch (Exception ex2) {
                        // Ignore
                    }
                    throw ex;
                }
            }
        });
    }
}
