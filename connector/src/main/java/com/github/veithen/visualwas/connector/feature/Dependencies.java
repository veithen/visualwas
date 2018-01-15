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
package com.github.veithen.visualwas.connector.feature;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.veithen.visualwas.connector.factory.Attributes;
import com.github.veithen.visualwas.connector.factory.ConnectorConfiguration;
import com.github.veithen.visualwas.connector.factory.ConnectorFactory;
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
