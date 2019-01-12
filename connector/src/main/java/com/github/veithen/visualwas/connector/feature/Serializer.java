/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2019 Andreas Veithen
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
package com.github.veithen.visualwas.connector.feature;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface defining the serialization/deserialization API used by the connector. A {@link Feature}
 * may use the {@link Configurator#setSerializer(Serializer)} method to override the default
 * implementation.
 */
public interface Serializer {
    String getRemoteClassName(Class<?> localClass);
    
    /**
     * Read an object from the given stream (using standard Java serialization). The implementation
     * must use the class loader returned by {@link InvocationContext#getClassLoader()} to resolve
     * classes referenced in the stream.
     * 
     * @param in
     *            the stream to read the object from
     * @param context
     *            the invocation context
     * @return the deserialized object
     * @throws IOException
     *             if an I/O error occurs
     * @throws ClassNotFoundException
     *             if the class of a serialized object cannot be found
     */
    Object readObject(InputStream in, InvocationContext context) throws IOException, ClassNotFoundException;

    /**
     * Write an object to the given stream (using standard Java serialization).
     * 
     * @param object
     *            the object to write
     * @param out
     *            the stream to write the object to
     * @param context
     *            the invocation context
     * @throws IOException
     *             if an I/O error occurs
     */
    void writeObject(Object object, OutputStream out, InvocationContext context) throws IOException;
}
