package com.github.veithen.visualwas.loader;

import java.lang.ref.WeakReference;
import java.net.URL;

public class Bundle {
    private final Realm realm;
    private final URL url;
    private WeakReference<BundleClassLoader> classLoader;

    public Bundle(Realm realm, URL url) {
        this.realm = realm;
        this.url = url;
    }
    
    public synchronized BundleClassLoader getClassLoader() {
        BundleClassLoader cl = classLoader == null ? null : classLoader.get();
        if (cl == null) {
            System.out.println("Creating class loader for " + url);
            cl = new BundleClassLoader(url, realm);
            classLoader = new WeakReference<BundleClassLoader>(cl);
        }
        return cl;
    }
}
