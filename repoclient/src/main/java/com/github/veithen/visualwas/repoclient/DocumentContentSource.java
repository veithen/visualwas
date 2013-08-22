package com.github.veithen.visualwas.repoclient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.Serializable;

import com.github.veithen.visualwas.connector.altclasses.AlternateClass;

@AlternateClass("com.ibm.websphere.management.repository.DocumentContentSource")
public class DocumentContentSource implements Serializable {
    private static final long serialVersionUID = -8662410128695608386L;

    private final Document document;
    private final Source source;

    public DocumentContentSource(Document document, Source source) {
        this.document = document;
        this.source = source;
    }
    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        PutField fields = stream.putFields();
        fields.put("document", document);
        // TODO: not correct; we need to upload the content via the file transfer service and then send a RepositoryInputStream
        ByteArrayInputStream bais;
        InputStream content = source.getInputStream();
        if (content instanceof ByteArrayInputStream) {
            bais = (ByteArrayInputStream)content;
        } else {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int c;
                while ((c = content.read(buffer)) != -1) {
                    baos.write(buffer, 0, c);
                }
                bais = new ByteArrayInputStream(baos.toByteArray());
            } finally {
                content.close();
            }
        }
        fields.put("source", bais);
        stream.writeFields();
    }
    
    public Document getDocument() {
        return document;
    }

    public Source getSource() {
        return source;
    }
}
