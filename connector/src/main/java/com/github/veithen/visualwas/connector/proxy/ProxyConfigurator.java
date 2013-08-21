package com.github.veithen.visualwas.connector.proxy;

import com.github.veithen.visualwas.connector.AdminService;

public interface ProxyConfigurator {
    /**
     * Register a proxy interface for a singleton MBean (i.e. an MBean that can exist only once in a
     * given WebSphere instance). Proxy instances for singleton MBeans are cached at the
     * {@link AdminService} level.
     * 
     * @param iface
     *            the proxy interface
     * @param locator
     *            the MBean locator
     */
    <T> void registerProxy(Class<T> iface, MBeanLocator locator);
}
