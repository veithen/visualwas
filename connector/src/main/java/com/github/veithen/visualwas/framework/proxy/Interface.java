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
package com.github.veithen.visualwas.framework.proxy;

/**
 * Describes an interface.
 *
 * <p>An interface is defined as a set of operations, each of which has:
 *
 * <ol>
 *   <li>A name.
 *   <li>A signature, i.e. a list of parameters, described by their types.
 *   <li>A response type.
 * </ol>
 *
 * When mapped to a Java interface, each operation may be represented by more than one method, in
 * which case these methods realize different invocation styles.
 */
public interface Interface<T> {
    Class<T> getInterface();

    Operation getOperation(String name);

    Operation[] getOperations();
}
