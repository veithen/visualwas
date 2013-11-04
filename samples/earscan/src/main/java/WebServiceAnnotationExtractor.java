import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class WebServiceAnnotationExtractor implements ClassVisitor {
    private final List<WebServiceImplementation> wsImplementations;
    private boolean isImplementation;
    private String className;

    public WebServiceAnnotationExtractor(List<WebServiceImplementation> wsImplementations) {
        this.wsImplementations = wsImplementations;
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        isImplementation = (access & (Opcodes.ACC_INTERFACE | Opcodes.ACC_ABSTRACT)) == 0;
        className = name.replace('/', '.');
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return isImplementation && desc.equals("Ljavax/jws/WebService;") ? new WebServiceAnnotationVisitor(wsImplementations, className) : null;
    }

    public void visitAttribute(Attribute arg0) {
        // TODO Auto-generated method stub
        
    }

    public void visitEnd() {
        // TODO Auto-generated method stub
        
    }

    public FieldVisitor visitField(int arg0, String arg1, String arg2,
            String arg3, Object arg4) {
        // TODO Auto-generated method stub
        return null;
    }

    public void visitInnerClass(String arg0, String arg1, String arg2, int arg3) {
        // TODO Auto-generated method stub
        
    }

    public MethodVisitor visitMethod(int arg0, String arg1, String arg2,
            String arg3, String[] arg4) {
        // TODO Auto-generated method stub
        return null;
    }

    public void visitOuterClass(String arg0, String arg1, String arg2) {
        // TODO Auto-generated method stub
        
    }

    public void visitSource(String arg0, String arg1) {
        // TODO Auto-generated method stub
        
    }
}
