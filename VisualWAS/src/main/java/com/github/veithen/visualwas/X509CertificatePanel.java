package com.github.veithen.visualwas;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.security.cert.X509Certificate;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openide.util.NbBundle;

public final class X509CertificatePanel extends JPanel {
    private static final long serialVersionUID = 1163922421062646559L;
    
    private final JTextField subjectField;
    private final JTextField issuerField;
    
    public X509CertificatePanel() {
        setLayout(new GridBagLayout());
        subjectField = addField(0, "LBL_Subject");
        issuerField = addField(1, "LBL_Issuer");
    }
    
    private JTextField addField(int y, String labelKey) {
        JLabel label = new JLabel(NbBundle.getMessage(X509CertificatePanel.class, labelKey));
        add(label, new GridBagConstraints(0, y, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(2, 0, 2, 0), 0, 0));
        JTextField field = new JTextField();
        add(field, new GridBagConstraints(1, y, 1, 1, 1.0, 0.0, WEST, BOTH, new Insets(2, 0, 2, 0), 0, 0));
        field.setEditable(false);
        label.setLabelFor(field);
        return field;
    }
    
    public void setCertificate(X509Certificate cert) {
        subjectField.setText(cert.getSubjectDN().toString());
        issuerField.setText(cert.getIssuerDN().toString());
    }
}
