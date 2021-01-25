/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2020 Andreas Veithen
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package com.github.veithen.visualwas.connector.mapped;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

import com.github.veithen.visualwas.connector.feature.InvocationContext;

/**
 * The {@link ObjectInputStream} implementation used when the {@link ClassMappingFeature} is
 * enabled. It gives the mapped classes access to the {@link InvocationContext}.
 */
public final class MappedObjectInputStream extends ObjectInputStream {
    private final ClassMapper classMapper;
    private final InvocationContext context;
    private boolean nextIsClassName;

    MappedObjectInputStream(InputStream in, ClassMapper classMapper, InvocationContext context)
            throws IOException {
        super(in);
        this.classMapper = classMapper;
        this.context = context;
    }

    public InvocationContext getInvocationContext() {
        return context;
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc)
            throws IOException, ClassNotFoundException {
        return Class.forName(desc.getName(), false, context.getClassLoader());
    }

    @SuppressWarnings("BanSerializableRead")
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
            result = classMapper.toLocalClass(result);
        }
        return result;
    }
}
