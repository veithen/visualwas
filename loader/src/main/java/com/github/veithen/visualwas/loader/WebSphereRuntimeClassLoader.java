package com.github.veithen.visualwas.loader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

public class WebSphereRuntimeClassLoader extends ClassLoader {
    private final Realm realm;
    
    public WebSphereRuntimeClassLoader(File wasHome, ClassLoader parent) throws IOException {
        super(parent);
        // Classes in the plugins may depend on bootstrap.jar (mainly for logging). Therefore we
        // need to set up a class loader with this library and use it as the parent class loader for
        // the realm. Note that the way this is set up implies that the classes in
        // bootstrap.jar are not visible through the WebSphereRuntimeClassLoader.
        this.realm = new Realm(wasHome, new URLClassLoader(new URL[] { new File(wasHome, "lib/bootstrap.jar").toURI().toURL() }, parent));
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
