import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import javax.activation.DataContentHandler;
import javax.activation.DataSource;

public class ObjectDataContentHandler implements DataContentHandler {
    @Override
    public Object getContent(DataSource paramDataSource) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getTransferData(DataFlavor paramDataFlavor,
            DataSource paramDataSource) throws UnsupportedFlavorException,
            IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeTo(Object obj, String mimeType, OutputStream os) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(obj);
        oos.flush();
    }
}
