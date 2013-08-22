package com.github.veithen.visualwas.connector.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ObjectName;

import com.github.veithen.visualwas.connector.AdminService;

final class ProxyInvocationHandler implements InvocationHandler {
    private final AdminService adminService;
    private final MBeanLocator locator;
    private ObjectName mbean;
    
    ProxyInvocationHandler(AdminService adminService, MBeanLocator locator) {
        this.adminService = adminService;
        this.locator = locator;
    }

    public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {
        Class<?>[] paramTypes = method.getParameterTypes();
        String[] signature = new String[paramTypes.length];
        for (int i=0; i<paramTypes.length; i++) {
            signature[i] = paramTypes[i].getName();
        }
        int attempts = 0;
        while (true) {
            synchronized (this) {
                if (mbean == null) {
                    mbean = locator.locateMBean(adminService);
                }
            }
            attempts++;
            try {
                return adminService.invoke(mbean, method.getName(), params, signature);
            } catch (InstanceNotFoundException ex) {
                if (attempts == 2) {
                    throw ex;
                } else {
                    synchronized (this) {
                        mbean = null;
                    }
                }
            } catch (MBeanException ex) {
                // MBean is a wrapper around exceptions thrown by MBeans. Unwrap the exception.
                throw ex.getCause();
            }
        }
    }
}
