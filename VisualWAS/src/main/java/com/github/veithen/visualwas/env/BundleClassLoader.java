package com.github.veithen.visualwas.env;

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
            return realm.findClass(name, resolve);
        }
    }

    public Class<?> findClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = findClass(name);
        if (resolve) {
            resolveClass(clazz);
        }
        return clazz;
    }
}
