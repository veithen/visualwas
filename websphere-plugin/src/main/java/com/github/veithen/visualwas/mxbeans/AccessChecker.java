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
package com.github.veithen.visualwas.mxbeans;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccessChecker {
    private static final Logger log = Logger.getLogger(AccessChecker.class.getName());
    
    private final Authorizer authorizer;
    private final Map<String,String> rules;

    public AccessChecker(Authorizer authorizer, Map<String,String> rules) {
        this.authorizer = authorizer;
        this.rules = rules;
    }

    public void checkAccess(String key) throws SecurityException {
        String role = rules.get(key);
        if (log.isLoggable(Level.FINE)) {
            log.fine("Access check; key=" + key + ", role=" + role);
        }
        if (role == null) {
            if (log.isLoggable(Level.FINE)) {
                log.fine("No access rule defined for method " + key);
            }
            throw new SecurityException("No access rule defined for method " + key);
        } else if (authorizer.checkAccess(role)) {
            log.fine("Access granted by authorizer");
        } else {
            log.fine("Access denied");
            throw new SecurityException("Access to " + key + " requires role " + role);
        }
    }
}
