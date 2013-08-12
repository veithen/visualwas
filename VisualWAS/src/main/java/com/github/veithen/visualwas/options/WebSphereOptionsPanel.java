package com.github.veithen.visualwas.options;

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.REMAINDER;
import static java.awt.GridBagConstraints.WEST;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

import com.github.veithen.visualwas.SimpleDocumentListener;
import com.github.veithen.visualwas.trust.TrustStore;

public class WebSphereOptionsPanel extends JPanel {
    private final JTextField wasHomeField;

    public WebSphereOptionsPanel(final WebSphereOptionsPanelController controller) {
        setLayout(new GridBagLayout());
        
        {
            JLabel label = new JLabel();
            Mnemonics.setLocalizedText(label, NbBundle.getMessage(WebSphereOptionsPanel.class, "LBL_WAS_home"));
            add(label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(2, 0, 2, 0), 0, 0));

            wasHomeField = new JTextField();
            label.setLabelFor(wasHomeField);
            wasHomeField.getDocument().addDocumentListener(new SimpleDocumentListener() {
                @Override
                protected void updated() {
                    controller.changed();
                }
            });
            add(wasHomeField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(2, 5, 2, 0), 0, 0));
            
            JButton btn = new JButton(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    chooser.setSelectedFile(new File(wasHomeField.getText()));
                    if (chooser.showOpenDialog(WebSphereOptionsPanel.this) == JFileChooser.APPROVE_OPTION) {
                        wasHomeField.setText(chooser.getSelectedFile().getAbsolutePath());
                    }
                }
            });
            Mnemonics.setLocalizedText(btn, NbBundle.getMessage(WebSphereOptionsPanel.class, "LBL_Browse"));
            add(btn, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(2, 5, 2, 0), 0, 0));
        }
        
        {
            JButton exportTrustStoreButton = new JButton(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    exportTrustStore();
                }
            });
            Mnemonics.setLocalizedText(exportTrustStoreButton, NbBundle.getMessage(WebSphereOptionsPanel.class, "LBL_Export_trust_store"));
            add(exportTrustStoreButton, new GridBagConstraints(0, 1, REMAINDER, 1, 0.0, 0.0, WEST, NONE, new Insets(0, 0, 0, 0), 0, 0));
        }
    }
    
    private void exportTrustStore() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter(NbBundle.getMessage(WebSphereOptionsPanel.class, "LBL_JKS_files"), "jks"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            JPasswordField pwd = new JPasswordField(10);  
            if (JOptionPane.showConfirmDialog(null, pwd, NbBundle.getMessage(WebSphereOptionsPanel.class, "LBL_Enter_password"), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    TrustStore.getInstance().export(chooser.getSelectedFile(), pwd.getPassword());
                } catch (Exception ex) {
                    // TODO
                    ex.printStackTrace();
                }
            }
        }
    }

    public boolean valid() {
        String wasHome = wasHomeField.getText();
        return wasHome.length() == 0 || new File(wasHome).isDirectory();
    }
    
    public String getWASHome() {
        String wasHome = wasHomeField.getText();
        return wasHome.length() == 0 ? null : wasHome;
    }
    
    public void setWASHome(String wasHome) {
        wasHomeField.setText(wasHome);
    }
}
