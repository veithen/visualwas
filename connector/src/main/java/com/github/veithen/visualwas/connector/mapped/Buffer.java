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
package com.github.veithen.visualwas.connector.mapped;

import java.io.UnsupportedEncodingException;

final class Buffer {
    private byte[] buffer = new byte[512];
    private int bufferLen;
    private int pos;

    private void ensureCapacity(int delta) {
        if (bufferLen+delta > buffer.length) {
            int newLength = buffer.length;
            do {
                newLength *= 2;
            } while (bufferLen+delta <= buffer.length);
            byte[] newBuffer = new byte[newLength];
            System.arraycopy(buffer, 0, newBuffer, 0, bufferLen);
            buffer = newBuffer;
        }
    }
    
    void write(int b) {
        ensureCapacity(1);
        buffer[bufferLen++] = (byte)b;
    }

    void write(byte[] b, int off, int len) {
        ensureCapacity(len);
        System.arraycopy(b, off, buffer, bufferLen, len);
        bufferLen += len;
    }
    
    int readByte() {
        if (pos == bufferLen) {
            throw new IllegalStateException();
        } else {
            return buffer[pos++] & 0xFF;
        }
    }
    
    int readUnsignedShort() {
        return (readByte() << 8) | readByte();
    }
    
    int readInt() {
        int result = 0;
        for (int i=0; i<4; i++) {
            result = (result << 8) | readByte(); 
        }
        return result;
    }
    
    long readLong() {
        long result = 0;
        for (int i=0; i<8; i++) {
            result = (result << 8) | readByte(); 
        }
        return result;
    }
    
    String readUTF() {
        try {
            int len = readUnsignedShort();
            String s = new String(buffer, pos, len, "UTF-8");
            pos += len;
            return s;
        } catch (UnsupportedEncodingException ex) {
            throw new Error(ex);
        }
    }
    
    void reset() {
        if (pos != bufferLen) {
            throw new IllegalArgumentException();
        }
        bufferLen = 0;
        pos = 0;
    }
}
