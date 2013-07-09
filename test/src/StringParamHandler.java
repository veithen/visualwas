import javax.xml.namespace.QName;

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMText;

public class StringParamHandler extends ParamHandler {
    public StringParamHandler(String name) {
        super(name);
    }

    @Override
    protected QName getXSIType() {
        return new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd");
    }

    @Override
    protected OMText createValueNode(OMFactory factory, Object value) {
        return factory.createOMText((String)value);
    }
}
