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
