package com.github.veithen.visualwas.repoclient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.Serializable;
import java.util.Properties;

import com.github.veithen.visualwas.connector.feature.AlternateClass;

@AlternateClass("com.ibm.ws.management.filetransfer.FileTransferConfigImpl")
public class FileTransferConfig implements Serializable {
    private static final long serialVersionUID = -67969875264167885L;

    private String host;
    private int port;
    private boolean securityEnabled;
    
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        GetField fields = stream.readFields();
        securityEnabled = fields.get("securityEnabled", false);
        Properties serverProperties = (Properties)fields.get("serverProperties", null);
        host = serverProperties.getProperty("host");
        port = Integer.parseInt(serverProperties.getProperty(securityEnabled ? "secure_port" : "port"));
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isSecurityEnabled() {
        return securityEnabled;
    }
}
