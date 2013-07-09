import java.io.IOException;
import java.io.InputStream;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.MailcapCommandMap;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMText;

public class ObjectParamHandler extends ParamHandler {
    private static final CommandMap commandMap;
    
    static {
        InputStream in = ObjectParamHandler.class.getResourceAsStream("connector.mailcap");
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
    
    public ObjectParamHandler(String name, Class<?> type) {
        super(name);
        this.type = type;
    }

    @Override
    protected QName getXSIType() {
        return new QName("urn:AdminService", type.getName());
    }

    @Override
    protected OMText createValueNode(OMFactory factory, Object value) {
        DataHandler dh = new DataHandler(value, "application/x-java-object");
        dh.setCommandMap(commandMap);
        return factory.createOMText(dh, false);
    }
}
