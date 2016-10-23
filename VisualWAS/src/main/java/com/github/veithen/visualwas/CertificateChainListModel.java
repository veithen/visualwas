/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2016 Andreas Veithen
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
