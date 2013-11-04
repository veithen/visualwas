import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.wsdl.xml.WSDLLocator;

import org.xml.sax.InputSource;

public class WSDLResources {
    class WSDLLocatorImpl implements WSDLLocator {
        private final String name;
        private String latestImport;
        
        WSDLLocatorImpl(String name) {
            this.name = name;
        }
        
        private InputSource getInputSource(String name) {
            byte[] content = resources.get(name);
            if (content == null) {
                throw new WSDLNotFoundException(name);
            }
            return new InputSource(new ByteArrayInputStream(content));
        }
        
        public InputSource getBaseInputSource() {
            return getInputSource(name);
        }

        public String getBaseURI() {
            return name;
        }

        public InputSource getImportInputSource(String parentLocation, String importLocation) {
            try {
                latestImport = new URI("module", null, "/" + parentLocation, null).resolve(importLocation).getPath().substring(1);
            } catch (URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
            return getInputSource(latestImport);
        }

        public String getLatestImportURI() {
            return latestImport;
        }
        
        public void close() {
        }
    }
    
    final Map<String,byte[]> resources = new HashMap<String,byte[]>();
    
    public void add(String name, byte[] content) {
        resources.put(name, content);
    }
    
    public WSDLLocator getWsdl(String name) {
        return new WSDLLocatorImpl(name);
    }
}
