package com.github.veithen.visualwas.env;

import java.io.File;
import java.io.IOException;

public class WebSphereRuntimeClassLoader extends ClassLoader {
    private final Realm realm;
    
    public WebSphereRuntimeClassLoader(File wasHome, ClassLoader parent) throws IOException {
        super(parent);
        this.realm = new Realm(wasHome, parent);
    }
    
    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        try {
            return super.loadClass(name, resolve);
        } catch (ClassNotFoundException ex) {
            return realm.findClass(name, resolve);
        }
    }
}
