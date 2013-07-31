/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.veithen.visualwas;

import com.sun.tools.visualvm.core.properties.PropertiesPanel;
import com.sun.tools.visualvm.core.ui.components.Spacer;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

/**
 *
 * @author veithen
 */
public class WebSpherePropertiesPanel extends PropertiesPanel implements DocumentListener {
    private final JTextField hostField;
    private final JTextField portField;
//    private JCheckBox securityCheckbox;
//    private JLabel usernameLabel;
//    private JTextField usernameField;
//    private JLabel passwordLabel;
//    private JPasswordField passwordField;
//    private JCheckBox saveCheckbox;
    
    public WebSpherePropertiesPanel() {
        setLayout(new GridBagLayout());

        {
            JLabel hostLabel = new JLabel();
            Mnemonics.setLocalizedText(hostLabel, NbBundle.getMessage(WebSpherePropertiesPanel.class, "LBL_Host"));
            add(hostLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

            hostField = new JTextField();
            hostLabel.setLabelFor(hostField);
            hostField.getDocument().addDocumentListener(this);
            add(hostField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
        }

        {
            JLabel portLabel = new JLabel();
            Mnemonics.setLocalizedText(portLabel, NbBundle.getMessage(WebSpherePropertiesPanel.class, "LBL_Port"));
            add(portLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

            portField = new JTextField(6);
            portLabel.setLabelFor(portField);
            portField.getDocument().addDocumentListener(this);
            add(portField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
        }

/*
        // securityCheckbox
        securityCheckbox = new JCheckBox();
        Mnemonics.setLocalizedText(securityCheckbox, NbBundle.getMessage(
                DefaultCustomizer.class, "LBL_Use_security_credentials")); // NOI18N
        securityCheckbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                update();
            };
        });
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(15, 0, 0, 0);
        add(securityCheckbox, constraints);

        // usernameLabel
        usernameLabel = new JLabel();
        Mnemonics.setLocalizedText(usernameLabel, NbBundle.getMessage(
                DefaultCustomizer.class, "LBL_Username")); // NOI18N
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 0, 0, 0);
        add(usernameLabel, constraints);

        // usernameField
        usernameField = new JTextField();
        usernameLabel.setLabelFor(usernameField);
        usernameField.setPreferredSize(
                new Dimension(320, usernameField.getPreferredSize().height));
        usernameField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                update();
            }
            public void removeUpdate(DocumentEvent e) {
                update();
            }
            public void changedUpdate(DocumentEvent e) {
                update();
            }
        });
        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 4;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 0, 0);
        add(usernameField, constraints);

        // passwordLabel
        passwordLabel = new JLabel();
        Mnemonics.setLocalizedText(passwordLabel, NbBundle.getMessage(
                DefaultCustomizer.class, "LBL_Password")); // NOI18N
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(8, 0, 0, 0);
        add(passwordLabel, constraints);

        // passwordField
        passwordField = new JPasswordField();
        passwordLabel.setLabelFor(passwordField);
        passwordField.setPreferredSize(
                new Dimension(200, passwordField.getPreferredSize().height));
        passwordField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                update();
            }
            public void removeUpdate(DocumentEvent e) {
                update();
            }
            public void changedUpdate(DocumentEvent e) {
                update();
            }
        });
        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 5;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(8, 5, 0, 0);
        add(passwordField, constraints);

        // saveCheckbox
        saveCheckbox = new JCheckBox();   // NOI18N
        Mnemonics.setLocalizedText(saveCheckbox, NbBundle.getMessage(
                DefaultCustomizer.class, "LBL_Save_security_credentials")); // NOI18N
        saveCheckbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                update();
            };
        });
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 6;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(8, 30, 0, 0);
        add(saveCheckbox, constraints);

        // UI tweaks
        displaynameCheckbox.setBorder(connectionLabel.getBorder());
*/
        add(Spacer.create(), new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        
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
    }
}
