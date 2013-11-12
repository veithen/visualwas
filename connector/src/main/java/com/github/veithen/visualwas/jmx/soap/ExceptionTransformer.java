package com.github.veithen.visualwas.jmx.soap;

import java.io.IOException;

import com.github.veithen.visualwas.connector.Connector;

/**
 * Transforms exceptions thrown by the {@link Connector} methods into {@link IOException} objects
 * that can be thrown by the corresponding {@link MBeanServerConnection} methods.
 */
public interface ExceptionTransformer {
    ExceptionTransformer DEFAULT = new ExceptionTransformer() {
        @Override
        public IOException transform(ClassNotFoundException ex) {
            return new MissingClassException(ex);
        }
    };
    
    IOException transform(ClassNotFoundException ex);
}
