/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2018 Andreas Veithen
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
package com.github.veithen.visualwas.connector.transport.dummy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPMessage;
import org.w3c.dom.Document;

import com.github.veithen.visualwas.connector.Connector;
import com.github.veithen.visualwas.connector.factory.ConnectorConfiguration;
import com.github.veithen.visualwas.connector.factory.ConnectorFactory;
import com.github.veithen.visualwas.connector.feature.Feature;
import com.github.veithen.visualwas.connector.feature.Handler;
import com.github.veithen.visualwas.connector.feature.InvocationContext;
import com.github.veithen.visualwas.connector.feature.SOAPResponse;
import com.github.veithen.visualwas.connector.transport.Endpoint;
import com.github.veithen.visualwas.connector.transport.TransportConfiguration;
import com.github.veithen.visualwas.connector.transport.TransportFactory;
import com.google.common.util.concurrent.ListenableFuture;

public class DummyTransport implements Handler<SOAPEnvelope,SOAPResponse>, TransportFactory {
    public static final Endpoint ENDPOINT = new Endpoint("localhost", 8888, false);
    
    private static OMMetaFactory domMetaFactory = OMAbstractFactory.getMetaFactory(OMAbstractFactory.FEATURE_DOM);
    
    private final RequestMatcher requestMatcher;
    
    public DummyTransport(RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
    }

    public Connector createConnector(Feature... features) {
        return ConnectorFactory.getInstance().createConnector(DummyTransport.ENDPOINT, ConnectorConfiguration.custom().addFeatures(features).setTransportFactory(this).build(), null);
    }
    
    public void addExchange(URL request, URL response) throws IOException {
        SOAPMessage requestMessage;
        InputStream in = request.openStream();
        try {
            requestMessage = OMXMLBuilderFactory.createSOAPModelBuilder(domMetaFactory, in, null).getSOAPMessage();
            requestMessage.build();
        } finally {
            in.close();
        }
        requestMatcher.add(new Exchange((Document)requestMessage, new CannedResponse(response)));
    }
    
    public void addExchange(Class<?> relativeTo, String baseName) throws IOException {
        addExchange(relativeTo.getResource(baseName + "-request.xml"), relativeTo.getResource(baseName + "-response.xml"));
    }
    
    public void addExchanges(Class<?> relativeTo, String... baseNames) throws IOException {
        for (String baseName : baseNames) {
            addExchange(relativeTo, baseName);
        }
    }
    
    @Override
    public Handler<SOAPEnvelope,SOAPResponse> createHandler(Endpoint endpoint, TransportConfiguration config) {
        return this;
    }

    @Override
    public ListenableFuture<SOAPResponse> invoke(InvocationContext context, SOAPEnvelope request) {
        SOAPMessage clonedRequest = domMetaFactory.createStAXSOAPModelBuilder(request.getXMLStreamReader()).getSOAPMessage();
        return requestMatcher.match((Document)clonedRequest).produce(context.getExecutor());
    }
}
