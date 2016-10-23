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

import java.io.IOException;
import java.util.Set;

import javax.management.ObjectName;
import javax.management.QueryExp;

import com.github.veithen.visualwas.connector.AdminService;
import com.github.veithen.visualwas.connector.Callback;
import com.github.veithen.visualwas.connector.Handler;
import com.github.veithen.visualwas.connector.Invocation;
import com.github.veithen.visualwas.connector.description.OperationDescription;
import com.github.veithen.visualwas.connector.feature.Interceptor;
import com.github.veithen.visualwas.connector.feature.InvocationContext;

final class InvocationInterceptor implements Interceptor<Invocation,Object,Throwable> {
    static class CallbackImpl implements Callback<Object,Throwable> {
        private Object response;

        @Override
        public void onResponse(Object response) {
            this.response = response;
        }

        @Override
        public void onFault(Throwable fault) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onTransportError(IOException ex) {
            // TODO Auto-generated method stub
            
        }

        Object getResponse() {
            return response;
        }
    }
    
    private static final OperationDescription getServerMBeanOperation = AdminService.DESCRIPTION.getOperation("getServerMBean");
    private static final OperationDescription queryNamesOperation = AdminService.DESCRIPTION.getOperation("queryNames");
    
    private ObjectNameMapper mapper;
    
    @Override
    public void invoke(final InvocationContext context, Invocation invocation, final Callback<Object,Throwable> callback, final Handler<Invocation,Object,Throwable> nextHandler) {
        // TODO: this is not thread safe and only works with synchronous transports!
        if (mapper == null) {
            nextHandler.invoke(context, new Invocation(getServerMBeanOperation), new Callback<Object,Throwable>() {
                @Override
                public void onResponse(Object response) {
                    ObjectName serverMBean = (ObjectName)response;
                    mapper = new ObjectNameMapper(serverMBean.getKeyProperty("cell"), serverMBean.getKeyProperty("node"), serverMBean.getKeyProperty("process"));
                }
                
                @Override
                public void onFault(Throwable fault) {
                    callback.onFault(fault);
                }

                @Override
                public void onTransportError(IOException ex) {
                    callback.onTransportError(ex);
                }
            });
        }
        if (invocation.getOperation() == queryNamesOperation) {
            Object[] args = invocation.getArgs();
            try {
                callback.onResponse(mapper.query((ObjectName)args[0], (QueryExp)args[1], new QueryExecutor<ObjectName>() {
                    @Override
                    public Set<ObjectName> execute(ObjectName objectName, QueryExp queryExp) throws IOException {
                        CallbackImpl callback = new CallbackImpl();
                        nextHandler.invoke(context, new Invocation(queryNamesOperation, objectName, queryExp), callback);
                        return (Set<ObjectName>)callback.getResponse();
                    }
                }));
            } catch (IOException ex) {
                callback.onTransportError(ex);
            }
        }
    }
}
