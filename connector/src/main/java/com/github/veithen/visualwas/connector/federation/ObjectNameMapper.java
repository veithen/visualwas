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
package com.github.veithen.visualwas.connector.federation;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.QueryExp;

final class ObjectNameMapper implements Mapper<ObjectName> {
    private static final Set<String> nonRoutableDomains = new HashSet<String>(Arrays.asList("JMImplementation", "java.lang"));
    private static final Set<String> routingPropertyKeys = new HashSet<String>(Arrays.asList("cell", "node", "process"));
    
    private final ServerMBeanSource serverMBeanSource;
    private final Object routingPropertiesLock = new Object();
    private String cell;
    private String node;
    private String process;
    private final Set<ObjectName> nonRoutableMBeans = Collections.synchronizedSet(new HashSet<ObjectName>());
    private final Map<ObjectName,ObjectName> localToRemoteCache = new WeakHashMap<>();
    // TODO: for localToRemoteCache, WeakHashMap is a good choice, but not for remoteToLocalCache
    private final Map<ObjectName,ObjectName> remoteToLocalCache = new WeakHashMap<>();
    
    ObjectNameMapper(ServerMBeanSource serverMBeanSource) {
        this.serverMBeanSource = serverMBeanSource;
    }

    private void loadRoutingProperties() throws IOException {
        ObjectName serverMBean = serverMBeanSource.getServerMBean();
        cell = serverMBean.getKeyProperty("cell");
        node = serverMBean.getKeyProperty("node");
        process = serverMBean.getKeyProperty("process");
    }
    
    private String getCell() throws IOException {
        synchronized (routingPropertiesLock) {
            if (cell == null) {
                loadRoutingProperties();
            }
            return cell;
        }
    }
    
    private String getNode() throws IOException {
        synchronized (routingPropertiesLock) {
            if (node == null) {
                loadRoutingProperties();
            }
            return node;
        }
    }
    
    private String getProcess() throws IOException {
        synchronized (routingPropertiesLock) {
            if (process == null) {
                loadRoutingProperties();
            }
            return process;
        }
    }
    
    ObjectName localToRemote(ObjectName localName) throws IOException {
        if (nonRoutableDomains.contains(localName.getDomain()) || nonRoutableMBeans.contains(localName)) {
            return localName;
        } else {
            synchronized (localToRemoteCache) {
                ObjectName remoteName = localToRemoteCache.get(localName);
                if (remoteName == null) {
                    Hashtable<String,String> newProps = new Hashtable<String,String>(localName.getKeyPropertyList());
                    newProps.put("cell", getCell());
                    newProps.put("node", getNode());
                    newProps.put("process", getProcess());
                    try {
                        remoteName = new ObjectName(localName.getDomain(), newProps);
                        if (localName.isPropertyListPattern()) {
                            remoteName = new ObjectName(remoteName + ",*");
                        }
                    } catch (MalformedObjectNameException ex) {
                        // It's unlikely that we ever get here
                        throw new Error(ex);
                    }
                    localToRemoteCache.put(localName, remoteName);
                }
                return remoteName;
            }
        }
    }
    
    public ObjectName remoteToLocal(ObjectName remoteName) {
        if (nonRoutableMBeans.contains(remoteName)) {
            return remoteName;
        } else {
            synchronized (remoteToLocalCache) {
                ObjectName localName = remoteToLocalCache.get(remoteName);
                if (localName == null) {
                    Hashtable<String,String> props = remoteName.getKeyPropertyList();
                    Hashtable<String,String> newProps = new Hashtable<String,String>();
                    int routingPropertyKeyCount = 0;
                    for (Map.Entry<String,String> prop : props.entrySet()) {
                        String key = prop.getKey();
                        if (routingPropertyKeys.contains(key)) {
                            routingPropertyKeyCount++;
                        } else {
                            newProps.put(key, prop.getValue());
                        }
                    }
                    if (routingPropertyKeyCount == 0) {
                        nonRoutableMBeans.add(remoteName);
                        return remoteName;
                    } else if (routingPropertyKeyCount == 3) {
                        try {
                            localName = new ObjectName(remoteName.getDomain(), newProps);
                        } catch (MalformedObjectNameException ex) {
                            // It's unlikely that we ever get here
                            throw new Error(ex);
                        }
                    } else {
                        throw new IllegalArgumentException("Expected exactly 3 routing key properties");
                    }
                    remoteToLocalCache.put(remoteName, localName);
                }
                return localName;
            }
        }
    }
    
    Set<ObjectName> remoteToLocal(Set<ObjectName> remoteNames) {
        Set<ObjectName> result = new HashSet<ObjectName>();
        for (ObjectName name : remoteNames) {
            result.add(remoteToLocal(name));
        }
        return result;
    }
    
    Set<ObjectName> query(ObjectName objectName, QueryExp queryExp, QueryExecutor<ObjectName> queryExecutor) throws IOException {
        return query(objectName, queryExp, queryExecutor, this);
    }
    
    private <T> Set<T> query(ObjectName objectName, QueryExp queryExp, QueryExecutor<T> queryExecutor, Mapper<T> mapper) throws IOException {
        if (queryExp != null) {
            // TODO
            throw new UnsupportedOperationException();
        }
        if (objectName == null) {
            loadRoutingProperties();
            try {
                Set<T> results = new HashSet<T>();
                for (T result : queryExecutor.execute(new ObjectName("*:cell=" + getCell() + ",node=" + getNode() + ",process=" + getProcess() + ",*"), queryExp)) {
                    results.add(mapper.remoteToLocal(result));
                }
                for (String domain : nonRoutableDomains) {
                    results.addAll(queryExecutor.execute(new ObjectName(domain + ":*"), queryExp));
                }
                return results;
            } catch (MalformedObjectNameException ex) {
                throw new IOException(ex);
            }
        } else {
            // TODO
            throw new UnsupportedOperationException();
        }
    }
}
