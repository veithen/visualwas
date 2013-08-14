package com.github.veithen.visualwas.connector;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class ConfigurableObjectInputStream extends ObjectInputStream implements InvocationContextHolder {
    private final InvocationContext context;
    private boolean nextIsClassName;
    
    public ConfigurableObjectInputStream(InputStream in, InvocationContext context) throws IOException {
        super(in);
        this.context = context;
    }

    @Override
    public InvocationContext getInvocationContext() {
        return context;
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        return Class.forName(desc.getName(), false, context.getClassLoader());
    }

    @Override
    protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
        nextIsClassName = true;
        return super.readClassDescriptor();
    }

    @Override
    public String readUTF() throws IOException {
        String result = super.readUTF();
        if (nextIsClassName) {
            nextIsClassName = false;
            String replacementClass = context.getClassMapper().getReplacementClass(result);
            if (replacementClass != null) {
                result = replacementClass;
            }
        }
        return result;
    }
}
