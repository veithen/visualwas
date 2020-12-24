/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2020 Andreas Veithen
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
package com.github.veithen.visualwas.env;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import org.openide.util.NbPreferences;

import com.github.veithen.visualwas.connector.loader.ClassLoaderProvider;
import com.github.veithen.visualwas.loader.WebSphereRuntimeClassLoader;
import com.github.veithen.visualwas.options.Constants;

public final class WebSphereClassLoaderProvider
        implements ClassLoaderProvider, PreferenceChangeListener {
    private static final Logger log =
            Logger.getLogger(WebSphereClassLoaderProvider.class.getName());

    private static WebSphereClassLoaderProvider instance;

    private final Preferences prefs;
    private ClassLoader classLoader;

    private WebSphereClassLoaderProvider() {
        prefs = NbPreferences.forModule(WebSphereClassLoaderProvider.class);
        prefs.addPreferenceChangeListener(this);
    }

    public static synchronized WebSphereClassLoaderProvider getInstance() {
        if (instance == null) {
            instance = new WebSphereClassLoaderProvider();
        }
        return instance;
    }

    @Override
    public synchronized void preferenceChange(PreferenceChangeEvent event) {
        if (event.getKey().equals(Constants.PROP_KEY_WAS_HOME)) {
            classLoader = null;
        }
    }

    @Override
    public synchronized ClassLoader getClassLoader() {
        if (classLoader == null) {
            String wasHome = prefs.get(Constants.PROP_KEY_WAS_HOME, null);
            if (wasHome != null) {
                try {
                    classLoader =
                            new WebSphereRuntimeClassLoader(
                                    new File(wasHome),
                                    WebSphereClassLoaderProvider.class.getClassLoader());
                    if (log.isLoggable(Level.INFO)) {
                        log.info(
                                "Successfully created class loader for WebSphere runtime at "
                                        + wasHome);
                    }
                } catch (IOException ex) {
                    // Normally, the option panel does the necessary verifications, so that we
                    // should rarely get here
                    log.log(
                            Level.SEVERE,
                            "Unable to create the class loader for the WebSphere runtime",
                            ex);
                }
            }
            if (classLoader == null) {
                log.info("WebSphere runtime not available; using default class loader");
                // Always fall back to the plugin class loader
                classLoader = WebSphereClassLoaderProvider.class.getClassLoader();
            }
        }
        return classLoader;
    }
}
