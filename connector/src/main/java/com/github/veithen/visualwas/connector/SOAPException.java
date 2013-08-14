package com.github.veithen.visualwas.connector;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;

import com.github.veithen.visualwas.connector.feature.AlternateClass;

@AlternateClass("org.apache.soap.SOAPException")
public class SOAPException extends Exception {
    private static final long serialVersionUID = 2184028360451885666L;

    private String faultCode;
    
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        GetField fields = stream.readFields();
        faultCode = (String)fields.get("faultCode", null);
        initCause((Throwable)fields.get("targetException", null));
    }
}
