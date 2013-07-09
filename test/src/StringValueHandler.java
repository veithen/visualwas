import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMText;

public class StringValueHandler implements ValueHandler {
    public static final StringValueHandler INSTANCE = new StringValueHandler();
    
    private StringValueHandler() {}
    
    @Override
    public QName getXSIType() {
        return new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd");
    }

    @Override
    public OMText createValueNode(OMFactory factory, Object value) {
        return factory.createOMText((String)value);
    }

    @Override
    public Object extractValue(OMElement element) {
        return element.getText();
    }
}
