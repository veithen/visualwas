/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2020 Andreas Veithen
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
package com.github.veithen.visualwas.connector.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.ext.io.StreamCopyException;

/** {@link Blob} implementation that outputs the enclosed object using Java serialization. */
public class ObjectBlob implements Blob {
    private final Object object;
    private final InvocationContextImpl context;

    public ObjectBlob(Object object, InvocationContextImpl context) {
        this.object = object;
        this.context = context;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeTo(out);
        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public void writeTo(OutputStream out) throws StreamCopyException {
        try {
            context.getSerializer().writeObject(object, out, context);
        } catch (IOException ex) {
            throw new StreamCopyException(StreamCopyException.WRITE, ex);
        }
    }

    @Override
    public long getSize() {
        return -1;
    }
}
