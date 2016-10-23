/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2016 Andreas Veithen
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
package com.github.veithen.visualwas.connector.proxy;

import com.github.veithen.visualwas.connector.AdminService;

final class ProxyFactoryImpl implements ProxyFactory {
    private final AdminService adminService;
    
    ProxyFactoryImpl(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public <T> T createProxy(Class<T> iface, MBeanLocator locator) {
        return ProxyHelper.createProxy(adminService, iface, locator);
    }
}
