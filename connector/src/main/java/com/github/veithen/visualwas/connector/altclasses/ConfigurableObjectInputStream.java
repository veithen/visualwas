package com.github.veithen.visualwas.connector.altclasses;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

import com.github.veithen.visualwas.connector.InvocationContext;

/**
 * The {@link ObjectInputStream} implementation used when the {@link AlternateClassesFeature} is
 * enabled. It gives the alternate classes access to the {@link InvocationContext}.
 */
public final class ConfigurableObjectInputStream extends ObjectInputStream {
    private final ClassMapper classMapper;
    private final InvocationContext context;
    private boolean nextIsClassName;
    
    ConfigurableObjectInputStream(InputStream in, ClassMapper classMapper, InvocationContext context) throws IOException {
        super(in);
        this.classMapper = classMapper;
        this.context = context;
    }

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
            String replacementClass = classMapper.getReplacementClass(result);
            if (replacementClass != null) {
                result = replacementClass;
            }
        }
        return result;
    }
}
