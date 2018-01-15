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
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.BundleException;
import org.xml.sax.SAXException;

/**
 * Manages a set of OSGi bundles from which classes can be loaded.
 */
// TODO: merge this into WebSphereRuntimeClassLoader and clean up
final class Realm {
    private final Map<String,List<Bundle>> packageMap = new HashMap<String,List<Bundle>>();
    private final Map<String,Bundle> classMap = new HashMap<String,Bundle>();
    private final ClassLoader parentClassLoader;
    private final URL[] bootstrapURLs;
    private WeakReference<BootstrapClassLoader> bootstrapClassLoader;
    
    Realm(File wasHome, ClassLoader parentClassLoader) throws IOException {
        this.parentClassLoader = parentClassLoader;
        bootstrapURLs = new URL[] {
                new File(wasHome, "java/jre/lib/ibmcfw.jar").toURI().toURL(),
                new File(wasHome, "lib/bootstrap.jar").toURI().toURL() };
        File pluginDir = new File(wasHome, "plugins");
        File[] jars = pluginDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith(".jar");
            }
        });
        if (jars == null) {
            throw new FileNotFoundException(pluginDir + " doesn't exist or is not readable");
        }
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);
        SAXParser saxParser;
        try {
            saxParser = saxParserFactory.newSAXParser();
        } catch (Exception ex) {
            // We should never get here
            throw new Error(ex);
        }
        for (File jar : jars) {
            InputStream in = new FileInputStream(jar);
            try {
                ZipInputStream zin = new ZipInputStream(in);
                boolean pluginXmlProcessed = false;
                Bundle bundle = null;
                PluginXmlHandler handler = new PluginXmlHandler();
                ZipEntry entry;
                while ((bundle == null || !pluginXmlProcessed) && (entry = zin.getNextEntry()) != null) {
                    String name = entry.getName();
                    if (bundle == null && name.equals("META-INF/MANIFEST.MF")) {
                        bundle = new Bundle(this, jar.toURI().toURL());
                        Manifest manifest = new Manifest(zin);
                        String exportPackageAttr = manifest.getMainAttributes().getValue("Export-Package");
                        if (exportPackageAttr != null) {
                            ManifestElement[] exportPackageElements;
                            try {
                                exportPackageElements = ManifestElement.parseHeader("Export-Package", exportPackageAttr);
                            } catch (BundleException ex) {
                                throw new IOException("Invalid bundle manifest", ex);
                            }
                            for (ManifestElement exportPackage : exportPackageElements) {
                                String pkg = exportPackage.getValue();
                                List<Bundle> bundles = packageMap.get(pkg);
                                if (bundles == null) {
                                    bundles = new ArrayList<Bundle>();
                                    packageMap.put(pkg, bundles);
                                }
                                bundles.add(bundle);
                            }
                        }
                    } else if (!pluginXmlProcessed && name.equals("plugin.xml")) {
                        try {
                            saxParser.parse(zin, handler);
                            pluginXmlProcessed = true;
                        } catch (SAXException ex) {
                            // TODO: log warning message
                            ex.printStackTrace();
                        }
                    }
                }
                if (bundle != null && pluginXmlProcessed) {
                    for (String className : handler.getSerializables()) {
                        classMap.put(className, bundle);
                    }
                }
            } finally {
                in.close();
            }
        }
    }

    synchronized BootstrapClassLoader getParentClassLoader() {
        // Classes in the plugins may depend on bootstrap.jar (mainly for logging). Therefore we
        // need to set up a class loader with this library and use it as the parent class loader for
        // the realm. Note that the way this is set up implies that the classes in
        // bootstrap.jar are not visible through the WebSphereRuntimeClassLoader, except for classes in
        // packages exported by some bundle.
        BootstrapClassLoader cl = bootstrapClassLoader == null ? null : bootstrapClassLoader.get();
        if (cl == null) {
            cl = new BootstrapClassLoader(bootstrapURLs, parentClassLoader);
            bootstrapClassLoader = new WeakReference<BootstrapClassLoader>(cl);
        }
        return cl;
    }
    
    /**
     * Load a class from this realm. This method does not delegate to the parent class loader; if
     * the class is not exported by any of the bundles in this realm, a
     * {@link ClassNotFoundException} will be thrown.
     * 
     * @param name
     *            the class name
     * @param resolve
     *            indicates if the class should be resolved
     * @return the class object; never <code>null</code>
     * @throws ClassNotFoundException
     *             if the class could not be found
     */
    Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Bundle bundle0 = classMap.get(name);
        if (bundle0 != null) {
            return bundle0.getClassLoader().loadClassLocally(name, resolve);
        }
        List<Bundle> bundles = packageMap.get(name.substring(0, name.lastIndexOf('.')));
        if (bundles != null) {
            for (Bundle bundle : bundles) {
                try {
                    return bundle.getClassLoader().loadClassLocally(name, resolve);
                } catch (ClassNotFoundException ex) {
                    // Continue with next bundle
                }
            }
        }
        throw new ClassNotFoundException(name);
    }
}
