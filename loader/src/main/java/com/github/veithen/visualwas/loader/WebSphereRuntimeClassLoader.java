/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2018 Andreas Veithen
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

import java.io.File;
import java.io.IOException;

/**
 * Class loader that loads classes from a WebSphere Application Server installation. The class
 * loader uses the OSGi metadata (more precisely the {@code Export-Package} attribute) to determine
 * from which bundle a given class should be loaded. It internally creates a separate class loader
 * for each bundle. These class loaders are created lazily (so that only a limited number of class
 * loaders are created) and they are referenced by weak references so that they can be garbage
 * collected.
 */
public class WebSphereRuntimeClassLoader extends ClassLoader {
    private final Realm realm;
    
    /**
     * Constructor.
     * 
     * @param wasHome
     *            the WebSphere Application Server installation directory (e.g.
     *            {@code /opt/IBM/WebSphere/AppServer})
     * @param parent
     *            the parent class loader to use
     * @throws IOException
     */
    public WebSphereRuntimeClassLoader(File wasHome, ClassLoader parent) throws IOException {
        super(parent);
        this.realm = new Realm(wasHome, parent);
    }
    
    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        try {
            return super.loadClass(name, resolve);
        } catch (ClassNotFoundException ex) {
            return realm.loadClass(name, resolve);
        }
    }
}
