package com.github.veithen.visualwas.connector.proxy;

import java.io.IOException;

import javax.management.InstanceNotFoundException;
import javax.management.ObjectName;

import com.github.veithen.visualwas.connector.AdminService;

public interface MBeanLocator {
    ObjectName locateMBean(AdminService adminService) throws IOException, InstanceNotFoundException;
}
