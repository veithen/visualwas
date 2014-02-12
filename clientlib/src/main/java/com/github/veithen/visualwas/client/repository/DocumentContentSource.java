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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.Serializable;

import com.github.veithen.visualwas.connector.mapped.MappedClass;

@MappedClass("com.ibm.websphere.management.repository.DocumentContentSource")
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
