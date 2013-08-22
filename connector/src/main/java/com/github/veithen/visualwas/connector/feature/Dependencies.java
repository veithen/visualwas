package com.github.veithen.visualwas.connector.feature;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.veithen.visualwas.connector.ConnectorConfiguration;

/**
 * Annotation that declares the dependencies of a {@link Feature}. Dependencies are resolved by
 * {@link ConnectorConfiguration.Builder#build()}. If no feature of the given type is present, then
 * the builder will attempt to add one. This only works if the feature is a singleton. Singleton
 * features are expected to declare a public static final field with name <code>INSTANCE</code>
 * storing a reference to the singleton instance.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Dependencies {
    Class<? extends Feature>[] value();
}
