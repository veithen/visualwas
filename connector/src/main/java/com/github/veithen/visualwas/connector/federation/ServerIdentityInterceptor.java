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
package com.github.veithen.visualwas.connector.federation;

import javax.management.ObjectName;

import com.github.veithen.visualwas.connector.AdminService;
import com.github.veithen.visualwas.connector.feature.ContextPopulatingInterceptor;
import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

final class ServerIdentityInterceptor extends ContextPopulatingInterceptor<ServerIdentity> {
    ServerIdentityInterceptor() {
        super(ServerIdentity.class);
    }

    @Override
    protected ListenableFuture<ServerIdentity> produceValue(AdminService adminService) {
        return Futures.transform(
                adminService.getServerMBeanAsync(),
                new Function<ObjectName, ServerIdentity>() {
                    @Override
                    public ServerIdentity apply(ObjectName serverMBean) {
                        return new ServerIdentity(serverMBean.getKeyProperty("cell"), serverMBean.getKeyProperty("node"), serverMBean.getKeyProperty("process"));
                    }
                });
    }
}
