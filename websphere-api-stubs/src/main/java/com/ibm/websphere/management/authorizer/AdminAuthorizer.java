package com.ibm.websphere.management.authorizer;

public interface AdminAuthorizer {
    boolean checkAccess(String resource, String role);
}
