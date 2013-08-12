package com.github.veithen.visualwas.connector;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;

public class AnyTypeHandler implements TypeHandler {
    private static final QName XSI_TYPE = new QName("http://www.w3.org/2001/XMLSchema-instance", "type");
    
    private final ObjectHandler objectHandler;
    
    public AnyTypeHandler() {
        objectHandler = new ObjectHandler(Object.class);
    }
    
    @Override
    public QName setValue(OMElement element, Object value) {
        SimpleTypeHandler simpleTypeHandler = SimpleTypeHandler.getByJavaType(value.getClass());
        if (simpleTypeHandler != null) {
            return simpleTypeHandler.setValue(element, value);
        } else {
            return objectHandler.setValue(element, value);
        }
    }

    @Override
    public Object extractValue(OMElement element, ClassLoader classLoader) throws TypeHandlerException {
        QName type = element.resolveQName(element.getAttributeValue(XSI_TYPE));
        SimpleTypeHandler simpleTypeHandler = SimpleTypeHandler.getBySchemaType(type);
        return (simpleTypeHandler != null ? simpleTypeHandler : objectHandler).extractValue(element, classLoader);
    }
}
