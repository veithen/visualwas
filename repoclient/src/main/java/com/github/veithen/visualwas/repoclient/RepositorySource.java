package com.github.veithen.visualwas.repoclient;

import java.io.Serializable;

import com.github.veithen.visualwas.connector.feature.AlternateClass;

@AlternateClass("com.ibm.websphere.management.repository.RepositoryInputStream")
public abstract class RepositorySource implements Source, Serializable {
    private static final long serialVersionUID = 1984160752415086662L;
    
    private String srcPath;

    public String getSrcPath() {
        return srcPath;
    }
}
