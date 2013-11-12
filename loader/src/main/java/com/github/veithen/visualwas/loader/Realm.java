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

import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.BundleException;

/**
 * Manages a set of OSGi bundles from which classes can be loaded.
 */
// TODO: merge this into WebSphereRuntimeClassLoader and clean up
final class Realm {
    private final Map<String,List<Bundle>> packageMap = new HashMap<String,List<Bundle>>();
    private final ClassLoader parentClassLoader;
    private final URL[] bootstrapURLs;
    private WeakReference<BootstrapClassLoader> bootstrapClassLoader;
    
    Realm(File wasHome, ClassLoader parentClassLoader) throws IOException {
        this.parentClassLoader = parentClassLoader;
        bootstrapURLs = new URL[] { new File(wasHome, "lib/bootstrap.jar").toURI().toURL() };
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
        for (File jar : jars) {
            InputStream in = new FileInputStream(jar);
            try {
                ZipInputStream zin = new ZipInputStream(in);
                ZipEntry entry;
                while ((entry = zin.getNextEntry()) != null) {
                    if (entry.getName().equals("META-INF/MANIFEST.MF")) {
                        Manifest manifest = new Manifest(zin);
                        String exportPackageAttr = manifest.getMainAttributes().getValue("Export-Package");
                        if (exportPackageAttr != null) {
                            Bundle bundle = new Bundle(this, jar.toURI().toURL());
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
                        break;
                    }
                }
            } finally {
                in.close();
            }
        }
    }

    synchronized ClassLoader getParentClassLoader() {
        // Classes in the plugins may depend on bootstrap.jar (mainly for logging). Therefore we
        // need to set up a class loader with this library and use it as the parent class loader for
        // the realm. Note that the way this is set up implies that the classes in
        // bootstrap.jar are not visible through the WebSphereRuntimeClassLoader.
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
