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

import java.lang.reflect.UndeclaredThrowableException;

import org.apache.axiom.soap.SOAPBody;

import com.github.veithen.visualwas.connector.ConnectorException;
import com.github.veithen.visualwas.connector.feature.SOAPResponse;
import com.google.common.base.Function;

final class UnmarshallingCallback implements Function<SOAPResponse,Object> {
    private final OperationHandler operationHandler;
    private final TypeHandler faultReasonHandler;
    private final InvocationContextImpl context;
    
    UnmarshallingCallback(OperationHandler operationHandler, TypeHandler faultReasonHandler, InvocationContextImpl context) {
        this.operationHandler = operationHandler;
        this.faultReasonHandler = faultReasonHandler;
        this.context = context;
    }

    @Override
    public Object apply(SOAPResponse response) {
        try {
            try {
                SOAPBody body = response.getEnvelope().getBody();
                if (response.isFault()) {
                    try {
                        throw (Throwable)faultReasonHandler.extractValue(body.getFault().getReason(), context);
                    } catch (TypeHandlerException ex) {
                        throw new ConnectorException("The operation has thrown an exception, but it could not be deserialized", ex);
                    }
                } else {
                    try {
                        return operationHandler.processResponse(body.getFirstElement(), context);
                    } catch (OperationHandlerException ex) {
                        throw new ConnectorException("Invocation failed", ex);
                    }
                }
            } finally {
                // TODO: also need to guarantee this is called if an error occurs elsewhere
                response.discard();
            }
        } catch (Throwable ex) {
            throw new UndeclaredThrowableException(ex);
        }
    }
}
