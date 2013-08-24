package com.github.veithen.visualwas.connector;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;

import com.github.veithen.visualwas.connector.feature.Serializer;
import com.github.veithen.visualwas.connector.transport.Transport;

public class AdminServiceInvocationHandler implements InvocationHandler {
    private final OMMetaFactory metaFactory;
    private final Map<Method,OperationHandler> operationHandlers;
    // TODO: this will eventually depend on the class loader
    private final TypeHandler faultReasonHandler = new ObjectHandler(Throwable.class);
    private final Interceptor[] interceptors;
    private final Transport transport;
    private final ConnectorConfiguration config;
    private final Serializer serializer;
    private final Attributes attributes;

    public AdminServiceInvocationHandler(Map<Method,OperationHandler> operationHandlers, Interceptor[] interceptors,
            Transport transport, ConnectorConfiguration config, Serializer serializer, Attributes attributes) {
        metaFactory = OMAbstractFactory.getMetaFactory();
        this.operationHandlers = operationHandlers;
        this.interceptors = interceptors;
        this.transport = transport;
        this.config = config;
        this.serializer = serializer;
        this.attributes = attributes;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        InvocationContext context = new InvocationContext(config, serializer, attributes);
        OperationHandler operationHandler = operationHandlers.get(method);
        SOAPFactory factory = metaFactory.getSOAP11Factory();
        SOAPEnvelope request = factory.createSOAPEnvelope();
        SOAPHeader header = factory.createSOAPHeader(request);
        OMNamespace ns1 = factory.createOMNamespace("admin", "ns");
        header.addAttribute("WASRemoteRuntimeVersion", "8.5.0.2", ns1);
        header.addAttribute("JMXMessageVersion", "1.2.0", ns1);
        header.addAttribute("JMXVersion", "1.2.0", ns1);
        // TODO: need this to prevent Axiom from skipping serialization of the header
        header.addHeaderBlock("dummy", factory.createOMNamespace("urn:dummy", "p")).setMustUnderstand(false);
        SOAPBody body = factory.createSOAPBody(request);
        operationHandler.createRequest(body, args, context);
        for (Interceptor interceptor : interceptors) {
            interceptor.processRequest(request, context);
        }
        TransportCallbackImpl callback = new TransportCallbackImpl(operationHandler, faultReasonHandler, context);
        transport.send(request, callback);
        Throwable throwable = callback.getThrowable();
        if (throwable != null) {
            throw throwable;
        } else {
            return callback.getResult();
        }
    }
}
