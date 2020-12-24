/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2020 Andreas Veithen
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
package com.github.veithen.visualwas.jmx.soap;

import java.io.IOException;

import javax.management.MBeanServerConnection;

import com.github.veithen.visualwas.connector.Connector;

/**
 * Transforms exceptions thrown by the {@link Connector} methods into {@link IOException} objects
 * that can be thrown by the corresponding {@link MBeanServerConnection} methods.
 */
public interface ExceptionTransformer {
    ExceptionTransformer DEFAULT =
            new ExceptionTransformer() {
                @Override
                public IOException transform(ClassNotFoundException ex) {
                    return new MissingClassException(ex);
                }
            };

    IOException transform(ClassNotFoundException ex);
}
