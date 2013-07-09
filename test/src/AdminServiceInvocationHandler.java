import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;

public class AdminServiceInvocationHandler implements InvocationHandler {
    private final OMMetaFactory metaFactory;
    private final Map<Method,OperationHandler> operationHandlers;
    // TODO: this will eventually depend on the class loader
    private final ValueHandler faultReasonHandler = new ObjectValueHandler(Throwable.class);

    public AdminServiceInvocationHandler(Map<Method,OperationHandler> operationHandlers) {
        metaFactory = OMAbstractFactory.getMetaFactory();
        this.operationHandlers = operationHandlers;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        OperationHandler handler = operationHandlers.get(method);
        SOAPFactory factory = metaFactory.getSOAP11Factory();
        SOAPEnvelope request = factory.createSOAPEnvelope();
        SOAPHeader header = factory.createSOAPHeader(request);
        OMNamespace ns1 = factory.createOMNamespace("admin", "ns");
        header.addAttribute("WASRemoteRuntimeVersion", "8.5.0.2", ns1);
        header.addAttribute("JMXMessageVersion", "1.2.0", ns1);
        header.addAttribute("JMXVersion", "1.2.0", ns1);
        // TODO: need this to prevent Axiom from skipping serialization of the header
        header.addHeaderBlock("dummy", factory.createOMNamespace("urn:dummy", "p")).setMustUnderstand(false);
        SOAPBody body = factory.createSOAPBody(request);
        handler.createOMElement(body, args);
        HttpURLConnection conn = (HttpURLConnection)new URL("http://localhost:8879").openConnection();
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
        conn.connect();
        try {
            OutputStream out = conn.getOutputStream();
            OMOutputFormat format = new OMOutputFormat();
            format.setCharSetEncoding("UTF-8");
            request.serialize(out);
            out.close();
            InputStream in = conn.getResponseCode() == 200 ? conn.getInputStream() : conn.getErrorStream();
            SOAPEnvelope response = OMXMLBuilderFactory.createSOAPModelBuilder(in, "UTF-8").getSOAPEnvelope(); // TODO: encoding!
            if (response.hasFault()) {
                throw (Throwable)faultReasonHandler.extractValue(response.getBody().getFault().getReason());
            } else {
                response.serialize(System.out);
                return handler.processResponse(response.getBody().getFirstElement());
            }
        } finally {
            conn.disconnect();
        }
    }
}
