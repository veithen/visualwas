package com.github.veithen.visualwas.connector;

import com.github.veithen.visualwas.connector.feature.AdapterFactory;

final class AdapterHolder<T> {
    private final AdapterFactory<T> factory;
    private T adapter;
    
    AdapterHolder(AdapterFactory<T> factory) {
        this.factory = factory;
    }
    
    synchronized T getAdapter(AdminService adminService) {
        if (adapter == null) {
            adapter = factory.createAdapter(adminService);
        }
        return adapter;
    }
}
