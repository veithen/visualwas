package com.github.veithen.visualwas.repoclient;

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
