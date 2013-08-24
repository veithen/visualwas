package com.github.veithen.visualwas.connector.feature;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.veithen.visualwas.connector.Attributes;
import com.github.veithen.visualwas.connector.ConnectorConfiguration;
import com.github.veithen.visualwas.connector.ConnectorFactory;
import com.github.veithen.visualwas.connector.transport.Endpoint;

/**
 * Annotation that declares a dependency on a set of {@link Feature}s. This annotation can be used
 * in the following contexts:
 * <ul>
 * <li>On another {@link Feature} implementation. These dependencies are resolved by
 * {@link ConnectorConfiguration.Builder#build()}.
 * <li>On any class that is used as key in an {@link Attributes} instance. These dependencies are
 * resolved by
 * {@link ConnectorFactory#createConnector(Endpoint, ConnectorConfiguration, Attributes)}.
 * </ul>
 * When resolving dependencies, the code checks if a feature of the given type is already present in
 * the connector configuration. If no feature of the given type is found, then the code will attempt
 * to add it to the configuration. This only works if the feature is a singleton. Singleton features
 * are expected to declare a public static final field with name <code>INSTANCE</code> storing a
 * reference to the singleton instance.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Dependencies {
    Class<? extends Feature>[] value();
}
