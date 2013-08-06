/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.veithen.visualwas;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.NORTHWEST;
import static java.awt.GridBagConstraints.REMAINDER;
import static java.awt.GridBagConstraints.WEST;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

import com.sun.tools.visualvm.core.properties.PropertiesPanel;
import com.sun.tools.visualvm.core.ui.components.Spacer;

/**
 *
 * @author veithen
 */
public class WebSpherePropertiesPanel extends PropertiesPanel implements DocumentListener {
    private static final long serialVersionUID = -5821630337324177997L;
    
    private final JTextField hostField;
    private final JTextField portField;
    private final JCheckBox securityCheckbox;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JCheckBox saveCheckbox;
    
    public WebSpherePropertiesPanel() {
        setLayout(new GridBagLayout());

        {
            JLabel hostLabel = new JLabel();
            Mnemonics.setLocalizedText(hostLabel, NbBundle.getMessage(WebSpherePropertiesPanel.class, "LBL_Host"));
            add(hostLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(2, 0, 2, 0), 0, 0));

            hostField = new JTextField();
            hostLabel.setLabelFor(hostField);
            hostField.getDocument().addDocumentListener(this);
            add(hostField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(2, 5, 2, 0), 0, 0));
        }

        {
            JLabel portLabel = new JLabel();
            Mnemonics.setLocalizedText(portLabel, NbBundle.getMessage(WebSpherePropertiesPanel.class, "LBL_Port"));
            add(portLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(2, 0, 2, 0), 0, 0));

            portField = new JTextField(6);
            portLabel.setLabelFor(portField);
            portField.getDocument().addDocumentListener(this);
            add(portField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, WEST, NONE, new Insets(2, 5, 2, 0), 0, 0));
        }

        {
            securityCheckbox = new JCheckBox();
            Mnemonics.setLocalizedText(securityCheckbox, NbBundle.getMessage(WebSpherePropertiesPanel.class, "LBL_Security_enabled"));
            securityCheckbox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    update();
                };
            });
            add(securityCheckbox, new GridBagConstraints(0, 2, REMAINDER, 1, 0.0, 0.0, WEST, NONE, new Insets(8, 0, 2, 0), 0, 0));
        }

        {
            JLabel usernameLabel = new JLabel();
            Mnemonics.setLocalizedText(usernameLabel, NbBundle.getMessage(WebSpherePropertiesPanel.class, "LBL_Username"));
            add(usernameLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(2, 15, 2, 0), 0, 0));
    
            usernameField = new JTextField(12);
            usernameLabel.setLabelFor(usernameField);
            usernameField.getDocument().addDocumentListener(this);
            add(usernameField, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, WEST, NONE, new Insets(2, 5, 2, 0), 0, 0));
        }

        {
            JLabel passwordLabel = new JLabel();
            Mnemonics.setLocalizedText(passwordLabel, NbBundle.getMessage(WebSpherePropertiesPanel.class, "LBL_Password"));
            add(passwordLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(2, 15, 2, 0), 0, 0));
    
            passwordField = new JPasswordField(12);
            passwordLabel.setLabelFor(passwordField);
            passwordField.getDocument().addDocumentListener(this);
            add(passwordField, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, WEST, NONE, new Insets(2, 5, 2, 0), 0, 0));
        }

        {
            saveCheckbox = new JCheckBox();   // NOI18N
            Mnemonics.setLocalizedText(saveCheckbox, NbBundle.getMessage(WebSpherePropertiesPanel.class, "LBL_Save_security_credentials"));
            saveCheckbox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    update();
                };
            });
            add(saveCheckbox, new GridBagConstraints(0, 5, REMAINDER, 1, 0.0, 0.0, WEST, NONE, new Insets(2, 15, 2, 0), 0, 0));
        }
/*
        // UI tweaks
        displaynameCheckbox.setBorder(connectionLabel.getBorder());
*/
        add(Spacer.create(), new GridBagConstraints(0, 6, 2, 1, 1.0, 1.0, NORTHWEST, BOTH, new Insets(0, 0, 0, 0), 0, 0));
        
        update();
    }

    public String getHost() {
        return hostField.getText();
    }
    
    public int getPort() {
        try {
            return Integer.parseInt(portField.getText());
        } catch (NumberFormatException ex) {
            return -1;
        }
    }
    
    public String getUsername() {
        return securityCheckbox.isSelected() ? usernameField.getText() : null;
    }

    public String getPassword() {
        return securityCheckbox.isSelected() ? new String(passwordField.getPassword()) : null;
    }
    
    public boolean isSaveCredentials() {
        return securityCheckbox.isSelected() && saveCheckbox.isSelected();
    }
    
    @Override
    public void insertUpdate(DocumentEvent event) {
        update();
    }

    @Override
    public void removeUpdate(DocumentEvent event) {
        update();
    }

    @Override
    public void changedUpdate(DocumentEvent event) {
        update();
    }
    
    private void update() {
        int port = getPort();
        setSettingsValid(getHost().length() > 0 && port > 0 && port < 1<<16);
        
        boolean securityEnabled = securityCheckbox.isSelected();
        usernameField.setEnabled(securityEnabled);
        passwordField.setEnabled(securityEnabled);
        saveCheckbox.setEnabled(securityEnabled);
    }
}
