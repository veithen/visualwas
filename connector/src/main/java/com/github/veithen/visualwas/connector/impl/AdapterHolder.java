/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2019 Andreas Veithen
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
package com.github.veithen.visualwas.connector.impl;

import java.util.concurrent.Executor;

import com.github.veithen.visualwas.connector.AdminService;
import com.github.veithen.visualwas.connector.feature.AdapterFactory;
import com.github.veithen.visualwas.connector.feature.CloseListener;

final class AdapterHolder<T> {
    private final AdapterFactory<T> factory;
    private T adapter;
    
    AdapterHolder(AdapterFactory<T> factory) {
        this.factory = factory;
    }
    
    synchronized T getAdapter(AdminService adminService, Executor executor) {
        if (adapter == null) {
            adapter = factory.createAdapter(adminService, executor);
        }
        return adapter;
    }
    
    synchronized void closing() {
        if (adapter instanceof CloseListener) {
            ((CloseListener)adapter).closing();
        }
    }
}
