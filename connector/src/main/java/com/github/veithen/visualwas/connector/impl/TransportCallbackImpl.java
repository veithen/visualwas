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
package com.github.veithen.visualwas.connector.impl;

import org.apache.axiom.soap.SOAPEnvelope;

import com.github.veithen.visualwas.connector.ConnectorException;
import com.github.veithen.visualwas.connector.transport.TransportCallback;

final class TransportCallbackImpl implements TransportCallback {
    private final OperationHandler operationHandler;
    private final TypeHandler faultReasonHandler;
    private final InvocationContextImpl context;
    private Throwable throwable;
    private Object result;
    
    TransportCallbackImpl(OperationHandler operationHandler, TypeHandler faultReasonHandler, InvocationContextImpl context) {
        this.operationHandler = operationHandler;
        this.faultReasonHandler = faultReasonHandler;
        this.context = context;
    }

    @Override
    public void onResponse(SOAPEnvelope envelope) {
        try {
            result = operationHandler.processResponse(envelope.getBody().getFirstElement(), context);
        } catch (ClassNotFoundException ex) {
            throwable = ex;
        } catch (OperationHandlerException ex) {
            throwable = new ConnectorException("Invocation failed", ex);
        }
    }

    @Override
    public void onFault(SOAPEnvelope envelope) {
        try {
            throwable = (Throwable)faultReasonHandler.extractValue(envelope.getBody().getFault().getReason(), context);
        } catch (ClassNotFoundException ex) {
            throwable = ex;
        } catch (TypeHandlerException ex) {
            throwable = new ConnectorException("The operation has thrown an exception, but it could not be deserialized", ex);
        }
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public Object getResult() {
        return result;
    }
}
