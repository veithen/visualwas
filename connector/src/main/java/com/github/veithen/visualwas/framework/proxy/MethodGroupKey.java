/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2016 Andreas Veithen
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

import java.util.Arrays;

final class MethodGroupKey {
    private final String defaultOperationName;
    private final Class<?>[] signature;

    MethodGroupKey(String defaultOperationName, Class<?>[] signature) {
        this.defaultOperationName = defaultOperationName;
        this.signature = signature;
    }

    @Override
    public int hashCode() {
        return defaultOperationName.hashCode() + 31*Arrays.hashCode(signature);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof MethodGroupKey) {
            MethodGroupKey other = (MethodGroupKey)obj;
            return defaultOperationName.equals(other.defaultOperationName) && Arrays.equals(signature, other.signature);
        } else {
            return false;
        }
    }
}
