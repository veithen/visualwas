/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 Andreas Veithen
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
import java.io.IOException;
import java.io.InputStream;

public class CloseShieldInputStream extends InputStream {
    private final InputStream parent;

    public CloseShieldInputStream(InputStream parent) {
        this.parent = parent;
    }

    public void close() throws IOException {
        // Ignore
    }

    public int available() throws IOException {
        return parent.available();
    }

    public void mark(int readlimit) {
        parent.mark(readlimit);
    }

    public boolean markSupported() {
        return parent.markSupported();
    }

    public int read() throws IOException {
        return parent.read();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        return parent.read(b, off, len);
    }

    public int read(byte[] b) throws IOException {
        return parent.read(b);
    }

    public void reset() throws IOException {
        parent.reset();
    }

    public long skip(long n) throws IOException {
        return parent.skip(n);
    }
}
