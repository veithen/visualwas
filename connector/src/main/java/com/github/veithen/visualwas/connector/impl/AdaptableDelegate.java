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

    @Override
    public <T> T getAdapter(Class<T> clazz) {
        AdapterHolder<?> holder = adapters.get(clazz);
        return holder == null ? null : clazz.cast(holder.getAdapter(adminService));
    }
}
