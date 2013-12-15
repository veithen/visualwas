/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 Andreas Veithen
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
package com.github.veithen.visualwas.connector.impl;

import java.util.HashMap;
import java.util.Map;

import com.github.veithen.visualwas.connector.Adaptable;
import com.github.veithen.visualwas.connector.AdminService;
import com.github.veithen.visualwas.connector.feature.AdapterFactory;

final class AdaptableDelegate implements Adaptable {
    private final Map<Class<?>,AdapterHolder<?>> adapters = new HashMap<Class<?>,AdapterHolder<?>>();
    private AdminService adminService;

    <T> void registerAdapter(Class<T> iface, AdapterFactory<T> adapterFactory) {
        adapters.put(iface, new AdapterHolder<T>(adapterFactory));
    }
    
    void setAdminService(AdminService adminService) {
        if (this.adminService != null) {
            throw new IllegalStateException();
        }
        this.adminService = adminService;
    }

    void closing() {
        for (AdapterHolder<?> holder : adapters.values()) {
            holder.closing();
        }
    }
    
    @Override
    public <T> T getAdapter(Class<T> clazz) {
        AdapterHolder<?> holder = adapters.get(clazz);
        return holder == null ? null : clazz.cast(holder.getAdapter(adminService));
    }
}
