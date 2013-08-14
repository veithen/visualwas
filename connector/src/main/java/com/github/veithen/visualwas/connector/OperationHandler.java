package com.github.veithen.visualwas.connector;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPBody;

import com.github.veithen.visualwas.connector.loader.ClassMapper;

public class OperationHandler {
    private static final QName XSI_NIL = new QName("http://www.w3.org/2001/XMLSchema-instance", "nil", "xsi");
    
    private final String operationName;
    private final String requestElementName;
    private final String responseElementName;
    private final ParamHandler[] paramHandlers;
    private final TypeHandler returnValueHandler;
    
    public OperationHandler(String operationName, String requestElementName, String responseElementName, ParamHandler[] paramHandlers, TypeHandler returnValueHandler) {
        this.operationName = operationName;
        this.requestElementName = requestElementName;
        this.responseElementName = responseElementName;
        this.paramHandlers = paramHandlers;
        this.returnValueHandler = returnValueHandler;
    }

    public void createRequest(SOAPBody body, Object[] args, InvocationContext context) {
        OMFactory factory = body.getOMFactory();
        OMNamespace ns = factory.createOMNamespace("urn:AdminService", "ns");
        OMElement element = factory.createOMElement(requestElementName, ns, body);
        element.addAttribute("encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/", body.getNamespace());
        int paramCount = paramHandlers.length;
        if (paramCount > 0) {
            OMNamespace xsiNS = element.declareNamespace("http://www.w3.org/2001/XMLSchema-instance", "xsi");
            for (int i=0; i<paramCount; i++) {
                paramHandlers[i].createOMElement(element, xsiNS, args[i], context);
            }
        }
    }
    
    public Object processResponse(OMElement response, InvocationContext context) throws OperationHandlerException {
        // TODO: check element names
        // TODO: check xsi:type???
        OMElement returnElement = response.getFirstElement();
        if ("true".equals(returnElement.getAttributeValue(XSI_NIL))) {
            return null;
        } else {
            try {
                return returnValueHandler.extractValue(returnElement, context);
            } catch (TypeHandlerException ex) {
                throw new OperationHandlerException("Failed to extract return value for operation " + operationName, ex);
            }
        }
    }
}
