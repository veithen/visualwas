import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.AnnotationVisitor;

public class WebServiceAnnotationVisitor implements AnnotationVisitor {
    private final List<WebServiceImplementation> wsImplementations;
    private final String className;
    private final Map<String,Object> attributes = new HashMap<String,Object>();
    
    public WebServiceAnnotationVisitor(List<WebServiceImplementation> wsImplementations, String className) {
        this.wsImplementations = wsImplementations;
        this.className = className;
    }
    
    public void visit(String name, Object value) {
        attributes.put(name, value);
    }

    public AnnotationVisitor visitAnnotation(String name, String desc) {
        return null;
    }

    public AnnotationVisitor visitArray(String name) {
        return null;
    }

    public void visitEnum(String name, String desc, String value) {
    }

    public void visitEnd() {
        wsImplementations.add(new WebServiceImplementation(className,
                (String)attributes.get("endpointInterface"),
                (String)attributes.get("wsdlLocation"),
                (String)attributes.get("targetNamespace"),
                (String)attributes.get("serviceName"),
                (String)attributes.get("portName")));
    }
}
