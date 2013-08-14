package com.github.veithen.visualwas.connector;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;

public class ConfigurableObjectOutputStream extends ObjectOutputStream implements InvocationContextHolder {
    private final InvocationContext context;
    private boolean nextIsClassName;

    public ConfigurableObjectOutputStream(OutputStream out, InvocationContext context) throws IOException {
        super(out);
        this.context = context;
    }

    @Override
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
            String originalClass = context.getClassMapper().getOriginalClass(str);
            if (originalClass != null) {
                str = originalClass;
            }
        }
        super.writeUTF(str);
    }
}
