package com.github.veithen.visualwas.connector;

import org.apache.axiom.soap.SOAPEnvelope;

import com.github.veithen.visualwas.connector.transport.TransportCallback;

final class TransportCallbackImpl implements TransportCallback {
    private final OperationHandler operationHandler;
    private final TypeHandler faultReasonHandler;
    private Throwable throwable;
    private Object result;
    
    TransportCallbackImpl(OperationHandler operationHandler, TypeHandler faultReasonHandler) {
        this.operationHandler = operationHandler;
        this.faultReasonHandler = faultReasonHandler;
    }

    @Override
    public void onResponse(SOAPEnvelope envelope) {
        result = operationHandler.processResponse(envelope.getBody().getFirstElement());
    }

    @Override
    public void onFault(SOAPEnvelope envelope) {
        throwable = (Throwable)faultReasonHandler.extractValue(envelope.getBody().getFault().getReason());
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public Object getResult() {
        return result;
    }
}
