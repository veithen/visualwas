/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2018 Andreas Veithen
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

import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import javax.management.InstanceNotFoundException;
import javax.management.ObjectName;

import com.github.veithen.visualwas.connector.AdminService;

public abstract class AbstractObjectNamePatternMBeanLocator implements MBeanLocator {
    private CompletableFuture<ObjectName> queryRequiredSingleton(AdminService adminService, ObjectName pattern) {
        return adminService.queryNamesAsync(pattern, null).thenApply(names -> {
            Iterator<ObjectName> it = names.iterator();
            if (it.hasNext()) {
                ObjectName mbean = it.next();
                if (it.hasNext()) {
                    throw new CompletionException(new InstanceNotFoundException("Found multiple MBeans matching " + pattern));
                } else {
                    return mbean;
                }
            } else {
                throw new CompletionException(new InstanceNotFoundException(pattern + " not found"));
            }
        });
    }
    
    @Override
    public final CompletableFuture<ObjectName> locateMBean(AdminService adminService) {
        return producePattern(adminService).thenCompose(pattern -> queryRequiredSingleton(adminService, pattern));
    }

    protected abstract CompletableFuture<ObjectName> producePattern(AdminService adminService);
}
