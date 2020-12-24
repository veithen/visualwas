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
package com.github.veithen.visualwas.options;

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
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.graalvm.visualvm.core.options.UISupport;
import org.graalvm.visualvm.core.ui.components.SectionSeparator;
import org.graalvm.visualvm.core.ui.components.Spacer;
import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

import com.github.veithen.visualwas.SimpleDocumentListener;
import com.github.veithen.visualwas.trust.TrustStore;

@SuppressWarnings("serial")
public class WebSphereOptionsPanel extends JPanel {
    private final JTextField wasHomeField;

    public WebSphereOptionsPanel(final WebSphereOptionsPanelController controller) {
        setLayout(new GridBagLayout());

        {
            SectionSeparator section =
                    UISupport.createSectionSeparator(
                            NbBundle.getMessage(WebSphereOptionsPanel.class, "LBL_Classloading"));
            add(
                    section,
                    new GridBagConstraints(
                            0,
                            0,
                            REMAINDER,
                            1,
                            0.0,
                            0.0,
                            WEST,
                            HORIZONTAL,
                            new Insets(0, 0, 5, 0),
                            0,
                            0));
        }

        {
            JLabel label = new JLabel();
            Mnemonics.setLocalizedText(
                    label, NbBundle.getMessage(WebSphereOptionsPanel.class, "LBL_WAS_home"));
            add(
                    label,
                    new GridBagConstraints(
                            0, 1, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(3, 15, 3, 0), 0, 0));

            wasHomeField = new JTextField();
            label.setLabelFor(wasHomeField);
            wasHomeField
                    .getDocument()
                    .addDocumentListener(
                            new SimpleDocumentListener() {
                                @Override
                                protected void updated() {
                                    controller.changed();
                                }
                            });
            add(
                    wasHomeField,
                    new GridBagConstraints(
                            1, 1, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(3, 10, 3, 0), 0, 0));

            JButton btn =
                    new JButton(
                            new AbstractAction() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    JFileChooser chooser = new JFileChooser();
                                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                                    chooser.setSelectedFile(new File(wasHomeField.getText()));
                                    if (chooser.showOpenDialog(WebSphereOptionsPanel.this)
                                            == JFileChooser.APPROVE_OPTION) {
                                        wasHomeField.setText(
                                                chooser.getSelectedFile().getAbsolutePath());
                                    }
                                }
                            });
            Mnemonics.setLocalizedText(
                    btn, NbBundle.getMessage(WebSphereOptionsPanel.class, "LBL_Browse"));
            add(
                    btn,
                    new GridBagConstraints(
                            2, 1, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(0, 10, 0, 0), 0, 0));
        }

        {
            SectionSeparator section =
                    UISupport.createSectionSeparator(
                            NbBundle.getMessage(
                                    WebSphereOptionsPanel.class, "LBL_Truststore_utilities"));
            add(
                    section,
                    new GridBagConstraints(
                            0,
                            2,
                            REMAINDER,
                            1,
                            0.0,
                            0.0,
                            WEST,
                            HORIZONTAL,
                            new Insets(15, 0, 5, 0),
                            0,
                            0));
        }

        {
            JButton exportTrustStoreButton =
                    new JButton(
                            new AbstractAction() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    exportTrustStore();
                                }
                            });
            Mnemonics.setLocalizedText(
                    exportTrustStoreButton,
                    NbBundle.getMessage(WebSphereOptionsPanel.class, "LBL_Export_trust_store"));
            add(
                    exportTrustStoreButton,
                    new GridBagConstraints(
                            0,
                            3,
                            REMAINDER,
                            1,
                            0.0,
                            0.0,
                            WEST,
                            NONE,
                            new Insets(3, 15, 3, 0),
                            0,
                            0));
        }

        add(
                Spacer.create(),
                new GridBagConstraints(
                        0,
                        4,
                        REMAINDER,
                        1,
                        1.0,
                        1.0,
                        NORTHWEST,
                        BOTH,
                        new Insets(0, 0, 0, 0),
                        0,
                        0));
    }

    private void exportTrustStore() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(
                new FileNameExtensionFilter(
                        NbBundle.getMessage(WebSphereOptionsPanel.class, "LBL_JKS_files"), "jks"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            JPasswordField pwd = new JPasswordField(10);
            if (JOptionPane.showConfirmDialog(
                            null,
                            pwd,
                            NbBundle.getMessage(WebSphereOptionsPanel.class, "LBL_Enter_password"),
                            JOptionPane.OK_CANCEL_OPTION)
                    == JOptionPane.OK_OPTION) {
                try {
                    TrustStore.getInstance().export(chooser.getSelectedFile(), pwd.getPassword());
                } catch (IOException ex) {
                    // TODO
                    ex.printStackTrace();
                }
            }
        }
    }

    public boolean valid() {
        String wasHome = wasHomeField.getText();
        if (wasHome.length() != 0) {
            File wasHomeDir = new File(wasHome);
            if (!wasHomeDir.isDirectory()
                    || !new File(wasHomeDir, "plugins").isDirectory()
                    || !new File(wasHomeDir, "lib/bootstrap.jar").exists()) {
                return false;
            }
        }
        return true;
    }

    public String getWASHome() {
        String wasHome = wasHomeField.getText();
        return wasHome.length() == 0 ? null : wasHome;
    }

    public void setWASHome(String wasHome) {
        wasHomeField.setText(wasHome);
    }
}
