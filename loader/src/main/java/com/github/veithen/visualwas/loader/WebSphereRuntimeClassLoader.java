package com.github.veithen.visualwas.loader;

import java.io.File;
import java.io.IOException;

/**
 * Class loader that loads classes from a WebSphere Application Server installation. The class
 * loader uses the OSGi metadata (more precisely the <tt>Export-Package</tt> attribute) to determine
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
     *            <tt>/opt/IBM/WebSphere/AppServer</tt>)
     * @param parent
     *            the parent class loader to use
     * @throws IOException
     */
    public WebSphereRuntimeClassLoader(File wasHome, ClassLoader parent) throws IOException {
        super(parent);
        // Classes in the plugins may depend on bootstrap.jar (mainly for logging). Therefore we
        // need to set up a class loader with this library and use it as the parent class loader for
        // the realm. Note that the way this is set up implies that the classes in
        // bootstrap.jar are not visible through the WebSphereRuntimeClassLoader.
        this.realm = new Realm(wasHome, new BootstrapClassLoader(wasHome, parent));
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
