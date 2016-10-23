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
package com.github.veithen.visualwas.connector.impl;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;

import com.github.veithen.visualwas.connector.Callback;
import com.github.veithen.visualwas.connector.Handler;
import com.github.veithen.visualwas.connector.Invocation;
import com.github.veithen.visualwas.connector.feature.InvocationContext;

final class MarshallingHandler implements Handler<Invocation,Object,Throwable> {
    private static final OMMetaFactory metaFactory = OMAbstractFactory.getMetaFactory();
    
    // TODO: this will eventually depend on the class loader
    private final TypeHandler faultReasonHandler = new ObjectHandler(Throwable.class);
    private final Handler<SOAPEnvelope,SOAPEnvelope,SOAPEnvelope> soapHandler;

    public MarshallingHandler(Handler<SOAPEnvelope,SOAPEnvelope,SOAPEnvelope> soapHandler) {
        this.soapHandler = soapHandler;
    }

    @Override
    public void invoke(InvocationContext context, Invocation invocation, Callback<Object,Throwable> callback) {
        InvocationContextImpl contextImpl = (InvocationContextImpl)context;
        OperationHandler operationHandler = (OperationHandler)invocation.getOperation();
        SOAPFactory factory = metaFactory.getSOAP11Factory();
        SOAPEnvelope request = factory.createSOAPEnvelope();
        if (!operationHandler.isSuppressSOAPHeader()) {
            SOAPHeader header = factory.createSOAPHeader(request);
            OMNamespace ns1 = factory.createOMNamespace("admin", "ns");
            header.addAttribute("JMXMessageVersion", "1.2.0", ns1);
            header.addAttribute("JMXVersion", "1.2.0", ns1);
            // TODO: need this to prevent Axiom from skipping serialization of the header
            header.addHeaderBlock("dummy", factory.createOMNamespace("urn:dummy", "p")).setMustUnderstand(false);
        }
        SOAPBody body = factory.createSOAPBody(request);
        operationHandler.createRequest(body, invocation.getArgs(), contextImpl);
        soapHandler.invoke(context, request, new UnmarshallingCallback(operationHandler, faultReasonHandler, contextImpl, callback));
    }
}
