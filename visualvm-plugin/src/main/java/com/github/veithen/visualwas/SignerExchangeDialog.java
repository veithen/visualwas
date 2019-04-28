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
package com.github.veithen.visualwas;

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.security.cert.X509Certificate;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

import com.github.veithen.visualwas.trust.TrustStore;

@SuppressWarnings("serial")
public class SignerExchangeDialog extends JDialog {
    private boolean certificateAdded;
    
    public SignerExchangeDialog(Window window, X509Certificate[] chain) {
        super(window);
        
        setLayout(new GridBagLayout());
        
        JLabel label = new JLabel();
        Mnemonics.setLocalizedText(label, NbBundle.getMessage(SignerExchangeDialog.class, "LBL_untrusted_warning"));
        add(label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(2, 0, 2, 0), 0, 0));
        final JList<X509Certificate> certSelector = new JList<>(new CertificateChainListModel(chain));
        label.setLabelFor(certSelector);
        certSelector.setCellRenderer(new CertificateChainListCellRenderer());
        certSelector.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        add(certSelector, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        
        final X509CertificatePanel certPanel = new X509CertificatePanel();
        add(certPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        certSelector.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                certPanel.setCertificate(certSelector.getSelectedValue());
            }
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        JButton addButton = new JButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TrustStore.getInstance().addCertificate(certSelector.getSelectedValue());
                dispose();
                certificateAdded = true;
            }
        });
        Mnemonics.setLocalizedText(addButton, NbBundle.getMessage(SignerExchangeDialog.class, "LBL_Add_to_trust_store"));
        buttonPanel.add(addButton);
        JButton cancelButton = new JButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        Mnemonics.setLocalizedText(cancelButton, NbBundle.getMessage(SignerExchangeDialog.class, "LBL_Cancel"));
        buttonPanel.add(cancelButton);
        add(buttonPanel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        
        // Select the server certificate. If the user wants to add the root certificate or
        // an intermediate certificate, he needs to select one.
        // This also forces an update of the X509CertificatePanel.
        certSelector.setSelectedIndex(chain.length-1);
        
        setResizable(false);
        pack();
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        setModal(true);
    }
    
    /**
     * Show the dialog. This method blocks until the user closes the dialog.
     * 
     * @return <code>true</code> if the user has added a certificate to the trust store,
     *         <code>false</code> if the user has cancelled or closed the dialog
     */
    public boolean showDialog() {
        setVisible(true);
        return certificateAdded;
    }
}
