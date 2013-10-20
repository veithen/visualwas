package com.github.veithen.visualwas.loader;

import java.lang.ref.WeakReference;
import java.net.URL;

/**
 * Represents an OSGi bundle in the realm.
 */
final class Bundle {
    private final Realm realm;
    private final URL url;
    private WeakReference<BundleClassLoader> classLoader;

    /**
     * Constructor.
     * 
     * @param realm
     *            the realm to which this bundle belongs
     * @param url
     *            the URL of the bundle
     */
    Bundle(Realm realm, URL url) {
        this.realm = realm;
        this.url = url;
    }
    
    /**
     * Get the class loader for this bundle. This method creates the class loader on demand. The
     * class only keeps a weak reference to the class loader so that it can be garbage collected.
     * 
     * @return the class loader for this bundle
     */
    synchronized BundleClassLoader getClassLoader() {
        BundleClassLoader cl = classLoader == null ? null : classLoader.get();
        if (cl == null) {
            System.out.println("Creating class loader for " + url);
            cl = new BundleClassLoader(url, realm);
            classLoader = new WeakReference<BundleClassLoader>(cl);
        }
        return cl;
    }
}
