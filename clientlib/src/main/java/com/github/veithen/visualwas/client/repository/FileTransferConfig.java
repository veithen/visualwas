/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2019 Andreas Veithen
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package com.github.veithen.visualwas.client.repository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.Serializable;
import java.util.Properties;

import com.github.veithen.visualwas.connector.mapped.MappedClass;

@MappedClass("com.ibm.ws.management.filetransfer.FileTransferConfigImpl")
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
