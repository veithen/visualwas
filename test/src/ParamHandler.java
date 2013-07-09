import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMText;

public abstract class ParamHandler {
    private final String name;

    public ParamHandler(String name) {
        this.name = name;
    }
    
    protected abstract QName getXSIType();
    protected abstract OMText createValueNode(OMFactory factory, Object value);
    
    public void createOMElement(OMElement operationElement, OMNamespace xsiNS, Object value) {
        OMFactory factory = operationElement.getOMFactory();
        OMElement element = factory.createOMElement(name, null, operationElement);
        QName type = getXSIType();
        OMNamespace ns = element.findNamespace(type.getNamespaceURI(), null);
        if (ns == null) {
            ns = element.declareNamespace(type.getNamespaceURI(), type.getPrefix());
        }
        // TODO: parameter order of addAttribute is not consistent
        // TODO: there should be a method to add an attribute with a QName value
        element.addAttribute("type", ns.getPrefix() + ":" + type.getLocalPart(), xsiNS);
        element.addChild(createValueNode(factory, value));
    }
}
