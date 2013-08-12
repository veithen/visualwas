package com.github.veithen.visualwas.connector.loader;

import java.security.AccessController;
import java.security.PrivilegedAction;

public interface ClassLoaderProvider {
    static class TCCL implements ClassLoaderProvider, PrivilegedAction<ClassLoader> {
        TCCL() {}
        
        @Override
        public ClassLoader getClassLoader() {
            return AccessController.doPrivileged(this);
        }

        @Override
        public ClassLoader run() {
            return Thread.currentThread().getContextClassLoader();
        }
    }
    
    ClassLoaderProvider TCCL = new TCCL();
    
    ClassLoader getClassLoader();
}
