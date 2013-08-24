package com.github.veithen.visualwas.connector;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;

public abstract class SimpleTypeHandler implements TypeHandler {
    private static final Map<Class<?>,SimpleTypeHandler> handlerByJavaType = new HashMap<Class<?>,SimpleTypeHandler>();
    private static final Map<QName,SimpleTypeHandler> handlerBySchemaType = new HashMap<QName,SimpleTypeHandler>();

    static {
        register(String.class, new SimpleTypeHandler("string") {
            @Override
            protected String toString(Object value) {
                return (String)value;
            }
            
            @Override
            protected Object fromString(String s) {
                return s;
            }
        });
    }
    
    private final QName type;

    public SimpleTypeHandler(String type) {
        this.type = new QName("http://www.w3.org/2001/XMLSchema", type, "xsd");
    }

    private static void register(Class<?> javaType, SimpleTypeHandler handler) {
        handlerByJavaType.put(javaType, handler);
        handlerBySchemaType.put(handler.getType(), handler);
    }
    
    public static SimpleTypeHandler getByJavaType(Class<?> javaType) {
        return handlerByJavaType.get(javaType);
    }
    
    public static SimpleTypeHandler getBySchemaType(QName type) {
        return handlerBySchemaType.get(type);
    }
    
    public final QName getType() {
        return type;
    }

    @Override
    public final QName setValue(OMElement element, Object value, InvocationContextImpl context) {
        element.setText(toString(value));
        return type;
    }

    @Override
    public final Object extractValue(OMElement element, InvocationContextImpl context) {
        return fromString(element.getText());
    }
    
    protected abstract String toString(Object value);
    protected abstract Object fromString(String s);
}
