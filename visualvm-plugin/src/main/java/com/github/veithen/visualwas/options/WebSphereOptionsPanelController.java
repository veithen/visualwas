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
package com.github.veithen.visualwas.options;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.prefs.Preferences;

import javax.swing.JComponent;

import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;

import com.sun.tools.visualvm.core.options.UISupport;

public class WebSphereOptionsPanelController extends OptionsPanelController {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private final Preferences prefs = NbPreferences.forModule(WebSphereOptionsPanelController.class);
    private WebSphereOptionsPanel panel;
    private JComponent component;
    private boolean changed;

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    private WebSphereOptionsPanel getPanel() {
        if (panel == null) {
            panel = new WebSphereOptionsPanel(this);
        }
        return panel;
    }
    
    @Override
    public JComponent getComponent(Lookup lookup) {
        if (component == null) {
            component = UISupport.createScrollableContainer(getPanel());
        }
        return component;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public boolean isValid() {
        return getPanel().valid();
    }

    @Override
    public void update() {
        WebSphereOptionsPanel panel = getPanel();
        panel.setWASHome(prefs.get(Constants.PROP_KEY_WAS_HOME, null));
    }
    
    @Override
    public void applyChanges() {
        WebSphereOptionsPanel panel = getPanel();
        String wasHome = panel.getWASHome();
        if (wasHome != null) {
            prefs.put(Constants.PROP_KEY_WAS_HOME, wasHome);
        } else {
            prefs.remove(Constants.PROP_KEY_WAS_HOME);
        }
    }

    @Override
    public void cancel() {
    }

    void changed() {
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }
}
