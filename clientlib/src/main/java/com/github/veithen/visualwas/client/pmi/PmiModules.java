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
package com.github.veithen.visualwas.client.pmi;

/**
 * Defines constants for PMI modules and submodules registered by WebSphere Application Server.
 */
public final class PmiModules {
    private PmiModules() {}
    
    public static final String BEAN = "beanModule";
    
    public static final String BEAN_METHODS = "beanModule.methods";
    
    public static final String CONNECTION_POOL = "connectionPoolModule";
    
    public static final String SYSTEM = "systemModule";
    
    public static final String J2C = "j2cModule";
    
    public static final String J2C_DATA_SOURCE = "DataSource";
    
    public static final String J2C_CONNECTION_FACTORY = "ConnectionFactory";
    
    public static final String J2C_JMS_CONNECTIONS = "jmsConnections";
    
    public static final String THREAD_POOL = "threadPoolModule";
    
    public static final String TRANSACTION = "transactionModule";
    
    public static final String JVM_RUNTIME = "jvmRuntimeModule";
    
    public static final String JVMPI = "jvmpiModule";
    
    public static final String ORB = "orbPerfModule";
    
    public static final String ORB_INTERCEPTORS = "orbPerfModule.interceptors";
    
    public static final String WEB_APP = "webAppModule";
    
    public static final String WEB_APP_SERVLETS = "webAppModule.servlets";
    
    public static final String SESSIONS = "servletSessionsModule";
    
    public static final String CACHE = "cacheModule";
    
    public static final String CACHE_TEMPLATE = "cacheModule.template";
    
    public static final String PMI = "pmi";
    
    public static final String WSGW = "wsgwModule";
    
    public static final String WLM = "wlmModule";
    
    public static final String WLM_SERVER = "wlmModule.server";
    
    public static final String WLM_CLIENT = "wlmModule.client";
    
    public static final String WEBSERVICES = "webServicesModule";
    
    public static final String WEBSERVICES_SERVICES = "webServicesModule.services";

    public static final String EJB_ENTITY = "ejb.entity";
    
    public static final String EJB_STATEFUL = "ejb.stateful";
    
    public static final String EJB_STATELESS = "ejb.stateless";
    
    public static final String EJB_MESSAGEDRIVEN = "ejb.messageDriven";
}
