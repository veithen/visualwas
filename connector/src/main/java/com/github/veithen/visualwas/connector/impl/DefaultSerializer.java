/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2016 Andreas Veithen
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
package com.github.veithen.visualwas.connector.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.github.veithen.visualwas.connector.feature.InvocationContext;
import com.github.veithen.visualwas.connector.feature.Serializer;

final class DefaultSerializer implements Serializer {
    static final DefaultSerializer INSTANCE = new DefaultSerializer();
    
    private DefaultSerializer() {}

    @Override
    public String getRemoteClassName(Class<?> localClass) {
        return localClass.getName();
    }

    @Override
    public Object readObject(InputStream in, InvocationContext context) throws IOException, ClassNotFoundException {
        return new ConfigurableObjectInputStream(in, context.getClassLoader()).readObject();
    }

    @Override
    public void writeObject(Object object, OutputStream out, InvocationContext context) throws IOException {
        new ObjectOutputStream(out).writeObject(object);
    }
}
