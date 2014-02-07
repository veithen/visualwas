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
package com.github.veithen.visualwas.connector;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPMessage;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.github.veithen.visualwas.connector.factory.ConnectorConfiguration;
import com.github.veithen.visualwas.connector.factory.ConnectorFactory;
import com.github.veithen.visualwas.connector.transport.Endpoint;
import com.github.veithen.visualwas.connector.transport.Transport;
import com.github.veithen.visualwas.connector.transport.TransportCallback;
import com.github.veithen.visualwas.connector.transport.TransportConfiguration;
import com.github.veithen.visualwas.connector.transport.TransportFactory;

public class DummyTransport implements Transport, TransportFactory {
    public static final Endpoint ENDPOINT = new Endpoint("localhost", 8888, false);
    
    private static OMMetaFactory domMetaFactory = OMAbstractFactory.getMetaFactory(OMAbstractFactory.FEATURE_DOM);
    private SOAPMessage expectedRequest;
    private URL cannedResponse;
    
    public Connector createConnector() {
        return ConnectorFactory.getInstance().createConnector(DummyTransport.ENDPOINT, ConnectorConfiguration.custom().setTransportFactory(this).build(), null);
    }
    
    private void normalize(SOAPEnvelope env) {
        // TODO: this should eventually disappear
        Iterator it = env.getHeader().getChildrenWithNamespaceURI("urn:dummy");
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
    }
    
    public void expect(URL request, URL response) throws IOException {
        InputStream in = request.openStream();
        try {
            expectedRequest = OMXMLBuilderFactory.createSOAPModelBuilder(domMetaFactory, in, null).getSOAPMessage();
            expectedRequest.build();
        } finally {
            in.close();
        }
        cannedResponse = response;
    }
    
    @Override
    public Transport createTransport(Endpoint endpoint, TransportConfiguration config) {
        return this;
    }

    @Override
    public void send(SOAPEnvelope request, TransportCallback callback) throws IOException {
        if (expectedRequest == null) {
            throw new IllegalStateException();
        }
        try {
            SOAPMessage clonedRequestMessage = domMetaFactory.createStAXSOAPModelBuilder(request.getXMLStreamReader()).getSOAPMessage();
            normalize(clonedRequestMessage.getSOAPEnvelope());
            boolean ignoreWhitespace = XMLUnit.getIgnoreWhitespace();
            boolean ignoreAttributeOrder = XMLUnit.getIgnoreAttributeOrder();
            try {
                XMLUnit.setIgnoreWhitespace(true);
                XMLUnit.setIgnoreAttributeOrder(true);
                Diff diff = XMLUnit.compareXML((Document)expectedRequest, (Document)clonedRequestMessage);
                diff.overrideDifferenceListener(new DifferenceListener() {
                    @Override
                    public int differenceFound(Difference difference) {
                        if (difference.getId() == DifferenceConstants.NAMESPACE_PREFIX_ID) {
                            return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
                        } else {
                            return RETURN_ACCEPT_DIFFERENCE;
                        }
                    }
                    
                    @Override
                    public void skippedComparison(Node control, Node test) {
                    }
                });
                XMLAssert.assertXMLEqual(diff, true);
            } finally {
                XMLUnit.setIgnoreWhitespace(ignoreWhitespace);
                XMLUnit.setIgnoreAttributeOrder(ignoreAttributeOrder);
            }
            InputStream in = cannedResponse.openStream();
            try {
                callback.onResponse(OMXMLBuilderFactory.createSOAPModelBuilder(domMetaFactory, in, null).getSOAPEnvelope());
            } finally {
                in.close();
            }
        } finally {
            expectedRequest = null;
            cannedResponse = null;
        }
    }
}
