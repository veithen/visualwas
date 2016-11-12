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
package com.github.veithen.visualwas.trust;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.prefs.Preferences;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedTrustManager;

import org.openide.util.NbPreferences;

public final class TrustStore {
    private static final String PROP_KEY = "trustStore";
    
    private static TrustStore instance;
    
    private final Preferences prefs;
    
    private TrustStore() {
        prefs = NbPreferences.forModule(TrustStore.class);
    }
    
    public static TrustStore getInstance() {
        if (instance == null) {
            instance = new TrustStore();
        }
        return instance;
    }
    
    private KeyStore getTrustStore() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            byte[] trustStoreContent = prefs.getByteArray(PROP_KEY, null);
            trustStore.load(trustStoreContent == null ? null : new ByteArrayInputStream(trustStoreContent), new char[0]);
            return trustStore;
        } catch (IOException | GeneralSecurityException ex) {
            throw new TrustStoreError(ex);
        }
    }
    
    /**
     * Create a {@link TrustManager} that validates server certificates against this trust store.
     * The returned trust manager is configured to throw a {@link NotTrustedException} with the
     * certificates presented by the server if they are not trusted.
     * 
     * @return the trust manager
     * @throws GeneralSecurityException
     */
    public TrustManager createTrustManager() {
        try {
            KeyStore trustStore = getTrustStore();
            if (trustStore.aliases().hasMoreElements()) {
                TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmfactory.init(trustStore);
                TrustManager[] trustManagers = tmfactory.getTrustManagers();
                if (trustManagers.length != 1) {
                    throw new RuntimeException("Expected a TrustManager array with a single entry");
                }
                return new TrustManagerWrapper((X509ExtendedTrustManager)trustManagers[0]);
            } else {
                return new NoTrustManager();
            }
        } catch (GeneralSecurityException ex) {
            throw new TrustStoreError(ex);
        }
    }

    public void addCertificate(X509Certificate cert) {
        try {
            KeyStore trustStore = getTrustStore();
            trustStore.setCertificateEntry(String.valueOf(System.currentTimeMillis()), cert);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            trustStore.store(baos, new char[0]);
            prefs.putByteArray(PROP_KEY, baos.toByteArray());
        } catch (IOException | GeneralSecurityException ex) {
            throw new TrustStoreError(ex);
        }
    }
    
    public void export(File file, char[] password) throws IOException {
        try {
            KeyStore trustStore = getTrustStore();
            FileOutputStream out = new FileOutputStream(file);
            try {
                trustStore.store(out, password);
            } finally {
                out.close();
            }
        } catch (GeneralSecurityException ex) {
            throw new TrustStoreError(ex);
        }
    }
}
