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
package com.github.veithen.visualwas.mxbeans;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.websphere.management.authorizer.AdminAuthorizer;
import com.ibm.websphere.management.authorizer.AdminAuthorizerFactory;

public class AuthorizerImpl implements Authorizer {
    private static final Logger log = Logger.getLogger(AuthorizerImpl.class.getName());
    
    private final String resource;
    private AdminAuthorizer adminAuthorizer;

    public AuthorizerImpl(String resource) {
        this.resource = resource;
    }

    public boolean checkAccess(String role) {
        synchronized (this) {
            if (adminAuthorizer == null) {
                adminAuthorizer = AdminAuthorizerFactory.getAdminAuthorizer();
                if (log.isLoggable(Level.FINE)) {
                    log.fine("adminAuthorizer = " + adminAuthorizer);
                }
                if (adminAuthorizer == null) {
                    log.fine("Security service not initialized; access denied");
                    throw new SecurityException("Security service not initialized; access denied");
                }
            }
        }
        return adminAuthorizer.checkAccess(resource, role);
    }
}
