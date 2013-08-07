package com.github.veithen.visualwas.trust;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Exception thrown when the server certificate is not trusted. We use our own exception for this so
 * that we can collect the certificates presented by the server. That information is used by the
 * signer exchange dialog.
 */
public final class NotTrustedException extends CertificateException {
    private static final long serialVersionUID = -847004143081413172L;
    
    private final X509Certificate[] chain;

    public NotTrustedException(X509Certificate[] chain) {
        this.chain = chain;
    }

    public NotTrustedException(CertificateException cause, X509Certificate[] chain) {
        super(cause);
        this.chain = chain;
    }

    public X509Certificate[] getChain() {
        return chain;
    }
}
