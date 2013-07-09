import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.MailcapCommandMap;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMText;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;

public final class ObjectValueHandler implements ValueHandler {
    private static final CommandMap commandMap;
    
    static {
        InputStream in = ObjectValueHandler.class.getResourceAsStream("connector.mailcap");
        try {
            commandMap = new MailcapCommandMap(in);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
            }
        }
    }
    
    private final Class<?> type;
    
    public ObjectValueHandler(Class<?> type) {
        this.type = type;
    }

    @Override
    public QName getXSIType() {
        return new QName("urn:AdminService", type.getName());
    }

    @Override
    public OMText createValueNode(OMFactory factory, Object value) {
        DataHandler dh = new DataHandler(value, "application/x-java-object");
        dh.setCommandMap(commandMap);
        return factory.createOMText(dh, false);
    }

    @Override
    public Object extractValue(OMElement element) {
        // TODO: suboptimal because it caches the data
        XMLStreamReader reader = element.getXMLStreamReader(false);
        try {
            reader.next();
            DataHandler dh = XMLStreamReaderUtils.getDataHandlerFromElement(reader);
            return new ObjectInputStream(dh.getInputStream()).readObject();
        } catch (Exception ex) {
            // TODO
            throw new OMException(ex);
        }
    }
}
