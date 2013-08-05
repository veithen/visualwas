package com.github.veithen.visualwas.connector.security;

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeader;

import com.github.veithen.visualwas.connector.Interceptor;

public class BasicAuthInterceptor implements Interceptor {
    private final String username;
    private final String password;
    
    public BasicAuthInterceptor(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void processRequest(SOAPEnvelope request) {
        OMFactory factory = request.getOMFactory();
        SOAPHeader header = request.getOrCreateHeader();
        OMNamespace ns = factory.createOMNamespace("admin", "ns");
        header.addAttribute("SecurityEnabled", "true", ns);
        factory.createOMElement("username", null, header).setText(username);
        factory.createOMElement("password", null, header).setText(password);
        factory.createOMElement("LoginMethod", null, header).setText("BasicAuth");
    }
}
