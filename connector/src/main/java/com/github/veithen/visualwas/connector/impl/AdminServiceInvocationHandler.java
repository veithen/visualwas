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
package com.github.veithen.visualwas.connector.impl;

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

import com.github.veithen.visualwas.connector.factory.Attributes;
import com.github.veithen.visualwas.connector.factory.ConnectorConfiguration;
import com.github.veithen.visualwas.connector.feature.Interceptor;
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
        if (method.getDeclaringClass() == Object.class) {
            String methodName = method.getName();
            if (methodName.equals("toString")) {
                return "<AdminService proxy>@" + Integer.toHexString(System.identityHashCode(proxy));
            } else if (methodName.equals("equals")) {
                return proxy == args[0];
            } else if (methodName.equals("hashCode")) {
                return System.identityHashCode(proxy);
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            return internalInvoke(method, args);
        }
    }
    
    private Object internalInvoke(Method method, Object[] args) throws Throwable {
        InvocationContextImpl context = new InvocationContextImpl(config, serializer, attributes);
        OperationHandler operationHandler = operationHandlers.get(method);
        SOAPFactory factory = metaFactory.getSOAP11Factory();
        SOAPEnvelope request = factory.createSOAPEnvelope();
        SOAPHeader header = factory.createSOAPHeader(request);
        OMNamespace ns1 = factory.createOMNamespace("admin", "ns");
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
