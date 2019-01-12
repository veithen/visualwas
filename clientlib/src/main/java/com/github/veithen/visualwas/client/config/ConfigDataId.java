/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2019 Andreas Veithen
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
package com.github.veithen.visualwas.client.config;

import java.io.Serializable;

import com.github.veithen.visualwas.connector.mapped.MappedClass;

@MappedClass("com.ibm.websphere.management.configservice.ConfigDataId")
public final class ConfigDataId implements Serializable {
    private static final long serialVersionUID = 7618627032319811532L;

    private final String contextUri;
    private final String href;
    
    public ConfigDataId(String contextUri, String href) {
        this.contextUri = contextUri;
        this.href = href;
    }

    @Override
    public String toString() {
        return contextUri + "|" + href;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ConfigDataId) {
            ConfigDataId other = (ConfigDataId)obj;
            return other.contextUri.equals(contextUri) && other.href.equals(href);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
