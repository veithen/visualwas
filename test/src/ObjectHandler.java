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
import org.apache.axiom.util.stax.XMLStreamReaderUtils;

public final class ObjectHandler implements TypeHandler {
    private static final CommandMap commandMap;
    
    static {
        InputStream in = ObjectHandler.class.getResourceAsStream("connector.mailcap");
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
    
    public ObjectHandler(Class<?> type) {
        this.type = type;
    }

    @Override
    public QName setValue(OMElement element, Object value) {
        DataHandler dh = new DataHandler(value, "application/x-java-object");
        dh.setCommandMap(commandMap);
        element.addChild(element.getOMFactory().createOMText(dh, false));
        return new QName("urn:AdminService", type.getName());
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
