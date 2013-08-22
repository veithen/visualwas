package com.github.veithen.visualwas.repoclient;

import java.io.Serializable;

import org.apache.commons.codec.binary.Hex;

import com.github.veithen.visualwas.connector.mapped.MappedClass;

// TODO: implement equals and hashCode
@MappedClass("com.ibm.ws.management.repository.DocumentDigestImpl")
public class DocumentDigest implements Serializable {
    private static final long serialVersionUID = 3015221028590796750L;

    private byte[] digest;

    @Override
    public String toString() {
        return Hex.encodeHexString(digest);
    }
}
