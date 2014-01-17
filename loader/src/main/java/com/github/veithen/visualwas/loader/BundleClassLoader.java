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
 * Loads classes from a given bundle.
 */
final class BundleClassLoader extends URLClassLoader {
    private final Realm realm;
    
    BundleClassLoader(URL url, Realm realm) {
        super(new URL[] { url }, realm.getParentClassLoader());
        this.realm = realm;
    }
    
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        try {
            return loadClassLocally(name, resolve);
        } catch (ClassNotFoundException ex) {
            // A class loaded from this bundle may depend on classes loaded from other bundles
            // (OSGI imports). Therefore we need to delegate to the realm.
            return realm.loadClass(name, resolve);
        }
    }

    /**
     * Load a class from the bundle corresponding to this class loader. The method will first
     * attempt to load the class from the realm's parent class loader. It will never delegate to any
     * other bundle class loader. This method is only used when the bundle that contains a given
     * class is known in advance.
     * 
     * @param name
     *            the class name
     * @param resolve
     *            indicates if the class should be resolved
     * @return the class object; never <code>null</code>
     * @throws ClassNotFoundException
     *             if the class could not be found
     */
    Class<?> loadClassLocally(String name, boolean resolve) throws ClassNotFoundException {
        try {
            return super.loadClass(name, resolve);
        } catch (ClassNotFoundException ex) {
            Class<?> clazz = findLoadedClass(name);
            if (clazz == null) {
                clazz = findClass(name);
            }
            if (resolve) {
                resolveClass(clazz);
            }
            return clazz;
        }
    }
}
