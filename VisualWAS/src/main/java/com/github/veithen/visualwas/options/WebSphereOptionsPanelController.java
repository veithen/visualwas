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
