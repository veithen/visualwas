package com.github.veithen.visualwas.connector.proxy;

import java.lang.reflect.Proxy;

import com.github.veithen.visualwas.connector.AdminService;
import com.github.veithen.visualwas.connector.feature.AdapterFactory;

final class ProxyAdapterFactory<T> implements AdapterFactory<T> {
    private final Class<T> iface;
    private final MBeanLocator locator;

    ProxyAdapterFactory(Class<T> iface, MBeanLocator locator) {
        this.iface = iface;
        this.locator = locator;
    }

    @Override
    public T createAdapter(AdminService adminService) {
        // TODO: correct class loader?
        return iface.cast(Proxy.newProxyInstance(ProxyAdapterFactory.class.getClassLoader(), new Class<?>[] { iface }, new ProxyInvocationHandler(adminService, locator)));
    }
}
