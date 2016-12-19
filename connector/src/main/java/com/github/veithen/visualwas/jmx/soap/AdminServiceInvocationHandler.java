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
package com.github.veithen.visualwas.jmx.soap;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.github.veithen.visualwas.connector.AdminService;
import com.google.common.util.concurrent.ListenableFuture;

final class AdminServiceInvocationHandler implements InvocationHandler {
    private static final Map<Method,Method> methodMap;
    
    static {
        methodMap = new HashMap<>();
        for (Method syncMethod : AdminServiceSync.class.getMethods()) {
            try {
                methodMap.put(syncMethod, AdminService.class.getMethod(syncMethod.getName(), syncMethod.getParameterTypes()));
            } catch (NoSuchMethodException ex) {
                throw new NoSuchMethodError(ex.getMessage());
            }
        }
    }

    private final AdminService adminService;

    public AdminServiceInvocationHandler(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return ((ListenableFuture<?>)methodMap.get(method).invoke(adminService, args)).get();
        } catch (InvocationTargetException | ExecutionException ex) {
            throw ex.getCause();
        }
    }
}
