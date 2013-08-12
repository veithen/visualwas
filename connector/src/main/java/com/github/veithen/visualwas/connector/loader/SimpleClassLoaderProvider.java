package com.github.veithen.visualwas.connector.loader;

public class SimpleClassLoaderProvider implements ClassLoaderProvider {
    private final ClassLoader classLoader;

    public SimpleClassLoaderProvider(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }
}
