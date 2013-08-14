package com.github.veithen.visualwas.repoclient;

import com.github.veithen.visualwas.connector.feature.AlternateClass;

@AlternateClass("com.ibm.websphere.management.exception.DocumentNotFoundException")
public class DocumentNotFoundException extends Exception {
    private static final long serialVersionUID = -3977638828059323939L;
}
