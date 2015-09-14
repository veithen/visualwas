package com.github.veithen.visualwas.mxbeans;

public interface Authorizer {
    boolean checkAccess(String role);
}
