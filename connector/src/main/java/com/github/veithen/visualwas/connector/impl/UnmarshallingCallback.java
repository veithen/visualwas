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
package com.github.veithen.visualwas.connector.impl;

import java.util.concurrent.CompletableFuture;

import org.apache.axiom.soap.SOAPBody;

import com.github.veithen.visualwas.connector.ConnectorException;
import com.github.veithen.visualwas.connector.feature.SOAPResponse;
import com.google.common.util.concurrent.FutureCallback;

final class UnmarshallingCallback implements FutureCallback<SOAPResponse> {
    private final OperationHandler operationHandler;
    private final TypeHandler faultReasonHandler;
    private final InvocationContextImpl context;
    private final CompletableFuture<Object> future;
    
    UnmarshallingCallback(OperationHandler operationHandler, TypeHandler faultReasonHandler, InvocationContextImpl context, CompletableFuture<Object> future) {
        this.operationHandler = operationHandler;
        this.faultReasonHandler = faultReasonHandler;
        this.context = context;
        this.future = future;
    }

    @Override
    public void onSuccess(SOAPResponse response) {
        Object result = null;
        Throwable exception = null;
        try {
            SOAPBody body = response.getEnvelope().getBody();
            if (response.isFault()) {
                try {
                    exception = (Throwable)faultReasonHandler.extractValue(body.getFault().getReason(), context);
                } catch (TypeHandlerException ex) {
                    exception = new ConnectorException("The operation has thrown an exception, but it could not be deserialized", ex);
                }
            } else {
                try {
                    result = operationHandler.processResponse(body.getFirstElement(), context);
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
            future.completeExceptionally(exception);
        } else {
            future.complete(result);
        }
    }

    @Override
    public void onFailure(Throwable t) {
        future.completeExceptionally(t);
    }
}
