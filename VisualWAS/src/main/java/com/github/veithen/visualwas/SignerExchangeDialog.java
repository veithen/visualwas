package com.github.veithen.visualwas;

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

import com.github.veithen.visualwas.trust.TrustStore;

public class SignerExchangeDialog extends JDialog {
    private static final long serialVersionUID = 5822625643082961287L;

    private boolean certificateAdded;
    
    public SignerExchangeDialog(Window window, X509Certificate[] chain) {
        super(window);
        
        setLayout(new GridBagLayout());
        
        JLabel label = new JLabel();
        Mnemonics.setLocalizedText(label, NbBundle.getMessage(SignerExchangeDialog.class, "LBL_untrusted_warning"));
        add(label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(2, 0, 2, 0), 0, 0));
        final JComboBox<X509Certificate> certSelector = new JComboBox<X509Certificate>(chain);
        label.setLabelFor(certSelector);
        certSelector.setRenderer(new BasicComboBoxRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                return super.getListCellRendererComponent(list, ((X509Certificate)value).getSubjectDN().getName(), index, isSelected, cellHasFocus);
            }
        });
        add(certSelector, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        
        final X509CertificatePanel certPanel = new X509CertificatePanel();
        add(certPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
        certSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                certPanel.setCertificate((X509Certificate)certSelector.getSelectedItem());
            }
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        JButton addButton = new JButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    TrustStore.getInstance().addCertificate((X509Certificate)certSelector.getSelectedItem());
                } catch (GeneralSecurityException ex) {
                    // TODO: should not happen
                    ex.printStackTrace();
                }
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
        
        // Force an update of the X509CertificatePanel
        certSelector.setSelectedIndex(0);
        
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
