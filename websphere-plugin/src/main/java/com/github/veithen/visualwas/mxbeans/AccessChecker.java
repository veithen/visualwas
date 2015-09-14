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
