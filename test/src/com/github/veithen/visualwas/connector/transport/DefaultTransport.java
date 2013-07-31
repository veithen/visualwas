package com.github.veithen.visualwas.connector.transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPEnvelope;

public class DefaultTransport implements Transport {
    private final URL endpointUrl;
    
    public DefaultTransport(URL endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    @Override
    public void send(SOAPEnvelope request, TransportCallback callback) throws TransportException {
        try {
            HttpURLConnection conn = (HttpURLConnection)endpointUrl.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
            conn.connect();
            try {
                OutputStream out = conn.getOutputStream();
                OMOutputFormat format = new OMOutputFormat();
                format.setCharSetEncoding("UTF-8");
                // TODO: this should actually throw IOException
                request.serialize(out);
                out.close();
                boolean isOK = conn.getResponseCode() == 200;
                InputStream in = isOK ? conn.getInputStream() : conn.getErrorStream();
                SOAPEnvelope response = OMXMLBuilderFactory.createSOAPModelBuilder(in, "UTF-8").getSOAPEnvelope(); // TODO: encoding!
                if (isOK) {
                    callback.onResponse(response);
                } else {
                    callback.onFault(response);
                }
            } finally {
                conn.disconnect();
            }
        } catch (IOException ex) {
            throw new TransportException(ex);
        } catch (XMLStreamException ex) {
            throw new TransportException(ex);
        }
    }
}
