/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2020 Andreas Veithen
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

import java.lang.ref.WeakReference;
import java.net.URL;

/** Represents an OSGi bundle in the realm. */
final class Bundle {
    private final Realm realm;
    private final URL url;
    private WeakReference<BundleClassLoader> classLoader;

    /**
     * Constructor.
     *
     * @param realm the realm to which this bundle belongs
     * @param url the URL of the bundle
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
            classLoader = new WeakReference<>(cl);
        }
        return cl;
    }
}
