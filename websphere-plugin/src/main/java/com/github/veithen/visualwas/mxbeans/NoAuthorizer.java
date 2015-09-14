package com.github.veithen.visualwas.mxbeans;

import java.util.logging.Logger;

public class NoAuthorizer implements Authorizer {
    private static final Logger log = Logger.getLogger(NoAuthorizer.class.getName());
    
    public boolean checkAccess(String role) {
        log.fine("Admin security not enabled; access granted");
        return true;
    }
}
