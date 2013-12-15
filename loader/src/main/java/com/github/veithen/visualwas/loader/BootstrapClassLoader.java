/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 Andreas Veithen
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
package com.github.veithen.visualwas.loader;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Loads classes from <tt>bootstrap.jar</tt>. Delegates to the specified parent class loader, except
 * for classes that we need to override.
 */
final class BootstrapClassLoader extends URLClassLoader {
    BootstrapClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }
    
    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (name.equals("com.ibm.websphere.logging.WsLevel")) {
            // TODO: is ignoring resolve OK?
            return BootstrapClassLoader.class.getClassLoader().loadClass(name);
        } else {
            return super.loadClass(name, resolve);
        }
    }
}