import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMText;

public interface ValueHandler {
    QName getXSIType();
    OMText createValueNode(OMFactory factory, Object value);
    Object extractValue(OMElement element);
}
