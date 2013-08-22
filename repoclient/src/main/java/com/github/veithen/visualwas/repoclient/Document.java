package com.github.veithen.visualwas.repoclient;

import java.io.Serializable;

import com.github.veithen.visualwas.connector.mapped.MappedClass;

@MappedClass("com.ibm.websphere.management.repository.Document")
public final class Document implements Serializable {
    private static final long serialVersionUID = -6951974517862374821L;
    
    private final String docURI;
    private final DocumentDigest digest;
    
    public Document(String docURI, DocumentDigest digest) {
        this.docURI = docURI;
        this.digest = digest;
    }

    public String getDocURI() {
        return docURI;
    }

    public DocumentDigest getDigest() {
        return digest;
    }
}
