package com.github.veithen.visualwas.connector;

import org.apache.axiom.soap.SOAPEnvelope;

import com.github.veithen.visualwas.connector.transport.TransportCallback;

final class TransportCallbackImpl implements TransportCallback {
    private final OperationHandler operationHandler;
    private final TypeHandler faultReasonHandler;
    private final ClassLoader classLoader;
    private Throwable throwable;
    private Object result;
    
    TransportCallbackImpl(OperationHandler operationHandler, TypeHandler faultReasonHandler, ClassLoader classLoader) {
        this.operationHandler = operationHandler;
        this.faultReasonHandler = faultReasonHandler;
        this.classLoader = classLoader;
    }

    @Override
    public void onResponse(SOAPEnvelope envelope) {
        try {
            result = operationHandler.processResponse(envelope.getBody().getFirstElement(), classLoader);
        } catch (OperationHandlerException ex) {
            throwable = new ConnectorException("Invocation failed", ex);
        }
    }

    @Override
    public void onFault(SOAPEnvelope envelope) {
        try {
            throwable = (Throwable)faultReasonHandler.extractValue(envelope.getBody().getFault().getReason(), classLoader);
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
