package com.github.veithen.visualwas;

import java.security.cert.X509Certificate;

import javax.swing.AbstractListModel;

final class CertificateChainListModel extends AbstractListModel<X509Certificate> {
    private final X509Certificate[] chain;

    CertificateChainListModel(X509Certificate[] chain) {
        this.chain = chain;
    }

    public int getSize() {
        return chain.length;
    }

    public X509Certificate getElementAt(int index) {
        return chain[chain.length-index-1];
    }
}
