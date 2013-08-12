package com.github.veithen.visualwas.loader;

import java.net.URL;
import java.net.URLClassLoader;

public class BundleClassLoader extends URLClassLoader {
    private final Realm realm;
    
    public BundleClassLoader(URL url, Realm realm) {
        super(new URL[] { url }, realm.getParentClassLoader());
        this.realm = realm;
    }
    
    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        try {
            return super.loadClass(name, resolve);
        } catch (ClassNotFoundException ex) {
            return realm.loadClass(name, resolve);
        }
    }

    /**
     * Load a class locally from this class loader, i.e. without delegating to other class loaders.
     * This method is only used when the class loader that contains a given class is known in
     * advance.
     * 
     * @param name
     *            the class name
     * @param resolve
     *            indicates if the class should be resolved
     * @return the class object; never <code>null</code>
     * @throws ClassNotFoundException
     *             if the class could not be found
     */
    public Class<?> loadClassLocally(String name, boolean resolve) throws ClassNotFoundException {
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
