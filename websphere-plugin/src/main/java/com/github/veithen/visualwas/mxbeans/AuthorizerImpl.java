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
