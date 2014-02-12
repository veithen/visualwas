/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2014 Andreas Veithen
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
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.zip.GZIPInputStream;

import com.github.veithen.visualwas.connector.feature.InvocationContext;
import com.github.veithen.visualwas.connector.mapped.MappedClass;
import com.github.veithen.visualwas.connector.mapped.MappedObjectInputStream;
import com.github.veithen.visualwas.connector.security.Credentials;
import com.github.veithen.visualwas.connector.transport.TransportConfiguration;

@MappedClass("com.ibm.websphere.management.filetransfer.client.FileDownloadInputStream")
public class RemoteSource extends RepositorySource {
    private static final long serialVersionUID = 6521346973672823614L;

    private TransportConfiguration transportConfiguration;
    private Credentials credentials;
    private FileTransferConfig fileTransferConfig;
    private FileTransferOptions fileTransferOptions;

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        InvocationContext context = ((MappedObjectInputStream)stream).getInvocationContext();
        transportConfiguration = context.getTransportConfiguration();
        credentials = context.getAttribute(Credentials.class);
        GetField fields = stream.readFields();
        fileTransferConfig = (FileTransferConfig)fields.get("ftConfig", null);
        fileTransferOptions = (FileTransferOptions)fields.get("options", null);
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        boolean securityEnabled = fileTransferConfig.isSecurityEnabled();
        HttpURLConnection conn = transportConfiguration.createURLConnection(new URL(securityEnabled ? "https" : "http",
                fileTransferConfig.getHost(), fileTransferConfig.getPort(),
                "/FileTransfer/transfer/" + URLEncoder.encode(getSrcPath(), "UTF-8") + "?compress=" + fileTransferOptions.isCompress() + "&deleteOnCompletion=" + fileTransferOptions.isDeleteOnCompletion()));
        if (securityEnabled && credentials != null) {
            credentials.configure(conn);
        }
        conn.connect();
        // TODO: need to check HTTP status code and throw exception if necessary
        InputStream stream = conn.getInputStream();
        return fileTransferOptions.isCompress() ? new GZIPInputStream(stream) : stream;
    }
}
