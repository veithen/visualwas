package com.github.veithen.visualwas.connector;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;

public final class ObjectHandler implements TypeHandler {
    private final Class<?> type;
    
    public ObjectHandler(Class<?> type) {
        this.type = type;
    }

    @Override
    public QName setValue(OMElement element, Object value) {
        element.addChild(element.getOMFactory().createOMText(new ObjectDataHandler(value), false));
        return new QName("urn:AdminService", type.getName());
    }

    @Override
    public Object extractValue(OMElement element, ClassLoader classLoader) throws TypeHandlerException {
        // TODO: suboptimal because it caches the data
        XMLStreamReader reader = element.getXMLStreamReader(false);
        try {
            reader.next();
            DataHandler dh = XMLStreamReaderUtils.getDataHandlerFromElement(reader);
            return new ConfigurableObjectInputStream(dh.getInputStream(), classLoader).readObject();
        } catch (Exception ex) {
            throw new TypeHandlerException("Failed to deserialize object (expected type: " + type.getName() + ")", ex);
        }
    }
}
