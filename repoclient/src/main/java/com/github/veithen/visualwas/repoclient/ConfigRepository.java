package com.github.veithen.visualwas.repoclient;

public interface ConfigRepository {
    DocumentContentSource extract(String docURI);
    String[] listResourceNames(String parent, int type, int depth);
    DocumentDigest getDigest(String docURI);
}
