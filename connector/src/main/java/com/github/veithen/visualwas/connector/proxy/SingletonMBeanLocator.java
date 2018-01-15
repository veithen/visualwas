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
import java.util.Set;

import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.github.veithen.visualwas.connector.AdminService;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

public class SingletonMBeanLocator implements MBeanLocator {
    private final String type;

    public SingletonMBeanLocator(String type) {
        this.type = type;
    }

    @Override
    public ListenableFuture<ObjectName> locateMBean(final AdminService adminService) {
        final SettableFuture<ObjectName> result = SettableFuture.create();
        Futures.addCallback(
                adminService.getServerMBeanAsync(),
                new FutureCallback<ObjectName>() {
                    @Override
                    public void onSuccess(ObjectName serverMBean) {
                        ObjectName pattern;
                        try {
                            pattern = new ObjectName(serverMBean.getDomain() + ":type=" + type + ",cell=" + serverMBean.getKeyProperty("cell") + ",node=" + serverMBean.getKeyProperty("node") + ",process=" + serverMBean.getKeyProperty("process") + ",*");
                        } catch (MalformedObjectNameException ex) {
                            result.setException(ex);
                            return;
                        }
                        Futures.addCallback(
                                adminService.queryNamesAsync(pattern, null),
                                new FutureCallback<Set<ObjectName>>() {
                                    @Override
                                    public void onSuccess(Set<ObjectName> names) {
                                        Iterator<ObjectName> it = names.iterator();
                                        if (it.hasNext()) {
                                            ObjectName mbean = it.next();
                                            if (it.hasNext()) {
                                                result.setException(new InstanceNotFoundException("Found multiple MBeans of type " + type));
                                            } else {
                                                result.set(mbean);
                                            }
                                        } else {
                                            result.setException(new InstanceNotFoundException("No MBean of type " + type + " found"));
                                        }
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {
                                        result.setException(t);
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        result.setException(t);
                    }
                });
        return result;
    }
}
