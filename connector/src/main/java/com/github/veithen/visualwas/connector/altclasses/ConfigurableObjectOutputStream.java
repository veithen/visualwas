package com.github.veithen.visualwas.connector.altclasses;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;

import com.github.veithen.visualwas.connector.InvocationContext;

/**
 * The {@link ObjectOutputStream} implementation used when the {@link AlternateClassesFeature} is
 * enabled. It gives the alternate classes access to the {@link InvocationContext}.
 */
public final class ConfigurableObjectOutputStream extends ObjectOutputStream {
    private final ClassMapper classMapper;
    private final InvocationContext context;
    private boolean nextIsClassName;

    ConfigurableObjectOutputStream(OutputStream out, ClassMapper classMapper, InvocationContext context) throws IOException {
        super(out);
        this.classMapper = classMapper;
        this.context = context;
    }

    public InvocationContext getInvocationContext() {
        return context;
    }

    @Override
    protected void writeClassDescriptor(ObjectStreamClass desc) throws IOException {
        nextIsClassName = true;
        super.writeClassDescriptor(desc);
    }

    @Override
    public void writeUTF(String str) throws IOException {
        if (nextIsClassName) {
            nextIsClassName = false;
            String originalClass = classMapper.getOriginalClass(str);
            if (originalClass != null) {
                str = originalClass;
            }
        }
        super.writeUTF(str);
    }
}
