/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2018 Andreas Veithen
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
package com.github.veithen.visualwas.mxbeans;

import java.util.prefs.Preferences;

import org.openide.util.NbPreferences;
import org.sonatype.goodies.prefs.memory.MemoryPreferences;

public class MemoryPreferencesProvider implements NbPreferences.Provider {
    private final MemoryPreferences root = new MemoryPreferences();
    
    @Override
    public Preferences preferencesForModule(Class clazz) {
        return root.node(clazz.getPackage().getName().replace('.', '/'));
    }

    @Override
    public Preferences preferencesRoot() {
        return root;
    }
}
