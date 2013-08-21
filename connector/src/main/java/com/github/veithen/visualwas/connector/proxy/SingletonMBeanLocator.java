package com.github.veithen.visualwas.connector.proxy;

import java.io.IOException;
import java.util.Iterator;

import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.github.veithen.visualwas.connector.AdminService;

public class SingletonMBeanLocator implements MBeanLocator {
    private final String type;

    public SingletonMBeanLocator(String type) {
        this.type = type;
    }

    @Override
    public ObjectName locateMBean(AdminService adminService) throws IOException, InstanceNotFoundException {
        ObjectName serverMBean = adminService.getServerMBean();
        ObjectName pattern;
        try {
            pattern = new ObjectName(serverMBean.getDomain() + ":type=" + type + ",cell=" + serverMBean.getKeyProperty("cell") + ",node=" + serverMBean.getKeyProperty("node") + ",process=" + serverMBean.getKeyProperty("process") + ",*");
        } catch (MalformedObjectNameException ex) {
            // We should never get here
            throw new RuntimeException(ex);
        }
        ObjectName mbean;
        Iterator<ObjectName> it = adminService.queryNames(pattern, null).iterator();
        if (it.hasNext()) {
            mbean = it.next();
            if (it.hasNext()) {
                throw new InstanceNotFoundException("Found multiple MBeans matching " + pattern);
            } else {
                return mbean;
            }
        } else {
            throw new InstanceNotFoundException(pattern + " not found");
        }
    }
}
