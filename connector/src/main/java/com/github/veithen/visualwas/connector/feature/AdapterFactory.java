package com.github.veithen.visualwas.connector.feature;

import com.github.veithen.visualwas.connector.AdminService;

public interface AdapterFactory<T> {
    T createAdapter(AdminService adminService);
}
