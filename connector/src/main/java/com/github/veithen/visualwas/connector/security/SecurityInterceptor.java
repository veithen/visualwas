package com.github.veithen.visualwas.connector.security;

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeader;

import com.github.veithen.visualwas.connector.feature.Interceptor;
import com.github.veithen.visualwas.connector.feature.InvocationContext;

final class SecurityInterceptor implements Interceptor {
    static final SecurityInterceptor INSTANCE = new SecurityInterceptor();
    
    private SecurityInterceptor() {}

    @Override
    public void processRequest(SOAPEnvelope request, InvocationContext context) {
        Credentials credentials = context.getAttribute(Credentials.class);
        if (credentials != null) {
            OMFactory factory = request.getOMFactory();
            SOAPHeader header = request.getOrCreateHeader();
            OMNamespace ns = factory.createOMNamespace("admin", "ns");
            header.addAttribute("SecurityEnabled", "true", ns);
            if (credentials instanceof BasicAuthCredentials) {
                BasicAuthCredentials basicAuthCreds = (BasicAuthCredentials)credentials;
                factory.createOMElement("username", null, header).setText(basicAuthCreds.getUsername());
                factory.createOMElement("password", null, header).setText(basicAuthCreds.getPassword());
                factory.createOMElement("LoginMethod", null, header).setText("BasicAuth");
            } else {
                // TODO: proper exception
                throw new UnsupportedOperationException();
            }
        }
    }
}
