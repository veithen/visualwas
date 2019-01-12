/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2019 Andreas Veithen
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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;

import com.github.veithen.visualwas.connector.ConnectorException;
import com.github.veithen.visualwas.connector.feature.Handler;
import com.github.veithen.visualwas.connector.feature.InvocationContext;
import com.github.veithen.visualwas.connector.feature.SOAPResponse;
import com.github.veithen.visualwas.framework.proxy.Invocation;

final class MarshallingHandler implements Handler<Invocation,Object> {
    private static final OMMetaFactory metaFactory = OMAbstractFactory.getMetaFactory();
    
    // TODO: this will eventually depend on the class loader
    private final TypeHandler faultReasonHandler = new ObjectHandler(Throwable.class);
    private final Handler<SOAPEnvelope,SOAPResponse> soapHandler;

    public MarshallingHandler(Handler<SOAPEnvelope,SOAPResponse> soapHandler) {
        this.soapHandler = soapHandler;
    }

    @Override
    public CompletableFuture<Object> invoke(InvocationContext context, Invocation invocation) {
        InvocationContextImpl contextImpl = (InvocationContextImpl)context;
        OperationHandler operationHandler = invocation.getOperation().getAdapter(OperationHandler.class);
        SOAPFactory factory = metaFactory.getSOAP11Factory();
        SOAPEnvelope request = factory.createSOAPEnvelope();
        if (!operationHandler.isSuppressSOAPHeader()) {
            SOAPHeader header = factory.createSOAPHeader(request);
            OMNamespace ns1 = factory.createOMNamespace("admin", "ns");
            header.addAttribute("JMXMessageVersion", "1.2.0", ns1);
            header.addAttribute("JMXVersion", "1.2.0", ns1);
        }
        SOAPBody requestBody = factory.createSOAPBody(request);
        operationHandler.createRequest(requestBody, invocation.getParameters(), contextImpl);
        return soapHandler.invoke(context, request).thenApplyAsync(response -> {
            Object result = null;
            Throwable exception = null;
            try {
                SOAPBody responseBody = response.getEnvelope().getBody();
                if (response.isFault()) {
                    try {
                        exception = (Throwable)faultReasonHandler.extractValue(responseBody.getFault().getReason(), contextImpl);
                    } catch (TypeHandlerException ex) {
                        exception = new ConnectorException("The operation has thrown an exception, but it could not be deserialized", ex);
                    }
                } else {
                    try {
                        result = operationHandler.processResponse(responseBody.getFirstElement(), contextImpl);
                    } catch (OperationHandlerException ex) {
                        exception = new ConnectorException("Invocation failed", ex);
                    }
                }
            } catch (Throwable ex) {
                exception = ex;
            }
            try {
                response.discard();
            } catch (Throwable ex) {
                if (exception == null) {
                    exception = ex;
                }
            }
            if (exception != null) {
                throw new CompletionException(exception);
            } else {
                return result;
            }
        }, context.getExecutor());
    }
}
