/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2019 Andreas Veithen
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
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
