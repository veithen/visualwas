package com.github.veithen.visualwas.connector.feature;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that can be used on a {@link Feature} implementation to specify the configuration
 * interface exposed by a that feature. The feature must register an instance of the specified interface
 * using {@link Configurator#registerConfiguratorAdapter(Object)}. Other features can then access that
 * instance using {@link Configurator#getAdapter(Class)}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConfiguratorAdapter {
    Class<?> value();
}
