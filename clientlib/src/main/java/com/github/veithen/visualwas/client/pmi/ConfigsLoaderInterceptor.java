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
package com.github.veithen.visualwas.client.pmi;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import javax.management.InstanceNotFoundException;

import com.github.veithen.visualwas.connector.AdminService;
import com.github.veithen.visualwas.connector.feature.ContextPopulatingInterceptor;
import com.github.veithen.visualwas.connector.proxy.SingletonMBeanLocator;

final class ConfigsLoaderInterceptor extends ContextPopulatingInterceptor<Configs> {
    ConfigsLoaderInterceptor() {
        super(Configs.class);
    }

    @Override
    protected CompletableFuture<Configs> produceValue(final AdminService adminService) {
        return new SingletonMBeanLocator("Perf")
                .locateMBean(adminService)
                .thenCompose(perfMBean -> adminService.invokeAsync(perfMBean, "getConfigs", null, null))
                .exceptionally(t -> {
                    if (t instanceof CompletionException) {
                        t = t.getCause();
                    }
                    if (t instanceof InstanceNotFoundException) {
                        // This means that PMI is disabled.
                        return new PmiModuleConfig[0];
                    } else {
                        throw new CompletionException(t);
                    }
                })
                .thenApply(input -> new Configs((PmiModuleConfig[])input));
    }
}
