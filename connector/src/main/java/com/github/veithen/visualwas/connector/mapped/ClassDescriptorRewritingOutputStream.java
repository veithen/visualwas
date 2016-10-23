/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2016 Andreas Veithen
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
package com.github.veithen.visualwas.connector.mapped;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

final class ClassDescriptorRewritingOutputStream extends OutputStream {
    private final DataOutputStream out;
    private final ClassMapper classMapper;
    private final Buffer buffer = new Buffer();
    private boolean inClassDescriptor;
    
    ClassDescriptorRewritingOutputStream(OutputStream target, ClassMapper classMapper) {
        out = new DataOutputStream(target);
        this.classMapper = classMapper;
    }

    void startClassDescriptor() {
        inClassDescriptor = true;
    }
    
    void endClassDescriptor() throws IOException {
        inClassDescriptor = false;
        String localClass = buffer.readUTF();
        String remoteClass = classMapper.toRemoteClass(localClass);
        out.writeUTF(remoteClass);
        long suid = buffer.readLong();
        // If the class is an array, we need to recompute the serial version ID
        if (localClass != remoteClass && localClass.charAt(0) == '[') {
            suid = Utils.computeArraySUID(remoteClass);
        }
        out.writeLong(suid);
        out.writeByte(buffer.readByte());
        int nfields = buffer.readUnsignedShort();
        out.writeShort(nfields);
        for (int i=0; i<nfields; i++) {
            int typeCode = buffer.readByte();
            out.writeByte(typeCode);
            out.writeUTF(buffer.readUTF());
            if (typeCode == 'L' || typeCode == '[') {
                int type = buffer.readByte();
                out.writeByte(type);
                if (type == ObjectOutputStream.TC_STRING) {
                    String signature = buffer.readUTF();
                    int pos = 0;
                    while (signature.charAt(pos) == '[') {
                        pos++;
                    }
                    if (signature.charAt(pos) == 'L') {
                        pos++;
                        localClass = signature.substring(pos, signature.length()-1).replace('/', '.');
                        remoteClass = classMapper.toRemoteClass(localClass);
                        if (remoteClass != localClass) {
                            signature = signature.substring(0, pos) + remoteClass.replace('.', '/') + ";";
                        }
                    }
                    out.writeUTF(signature);
                } else if (type == ObjectOutputStream.TC_REFERENCE) {
                    out.writeInt(buffer.readInt());
                } else {
                    throw new IllegalStateException();
                }
            }
        }
        buffer.reset();
    }
    
    @Override
    public void write(int b) throws IOException {
        if (inClassDescriptor) {
            buffer.write(b);
        } else {
            out.write(b);
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        if (inClassDescriptor) {
            buffer.write(b, 0, b.length);
        } else {
            out.write(b);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (inClassDescriptor) {
            buffer.write(b, off, len);
        } else {
            out.write(b, off, len);
        }
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}
