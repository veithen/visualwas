/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2014 Andreas Veithen
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

import java.io.IOException;

import com.github.veithen.visualwas.connector.Callback;

public class SynchronousInvocationCallback implements Callback<Object,Throwable> {
    private Throwable throwable;
    private Object result;

    @Override
    public void onResponse(Object response) {
        result = response;
    }

    @Override
    public void onFault(Throwable fault) {
        throwable = fault;
    }

    @Override
    public void onTransportError(IOException ex) {
        throwable = ex;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public Object getResult() {
        return result;
    }
}
