package com.github.veithen.visualwas.env;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Realm {
    private final Map<String,Bundle> packageMap = new HashMap<String,Bundle>();
    private final ClassLoader parentClassLoader;
    
    public Realm(File wasHome, ClassLoader parentClassLoader) throws IOException {
        this.parentClassLoader = parentClassLoader;
        File pluginDir = new File(wasHome, "plugins");
        File[] jars = pluginDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith(".jar");
            }
        });
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
                            for (String val : exportPackageAttr.split(",")) {
                                int idx = val.indexOf(';');
                                String pkg = idx == -1 ? val : val.substring(0, idx);
                                packageMap.put(pkg.trim(), bundle);
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

    public ClassLoader getParentClassLoader() {
        return parentClassLoader;
    }
    
    public Class<?> findClass(String name, boolean resolve) throws ClassNotFoundException {
        Bundle bundle = packageMap.get(name.substring(0, name.lastIndexOf('.')));
        if (bundle == null) {
            throw new ClassNotFoundException(name);
        } else {
            return bundle.getClassLoader().findClass(name, resolve);
        }
    }
}
