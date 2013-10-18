package com.github.veithen.visualwas;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openide.util.NbBundle;

public final class X509CertificatePanel extends JPanel {
    private static final long serialVersionUID = 1163922421062646559L;
    
    private static final Logger log = Logger.getLogger(X509CertificatePanel.class.getName());
    
    private final JTextField subjectField;
    private final JTextField issuerField;
    private final JTextField notBefore;
    private final JTextField notAfter;
    private final JTextField fingerprint;
    
    public X509CertificatePanel() {
        setLayout(new GridBagLayout());
        subjectField = addField(0, "LBL_Subject");
        issuerField = addField(1, "LBL_Issuer");
        notBefore = addField(2, "LBL_NotBefore");
        notAfter = addField(3, "LBL_NotAfter");
        fingerprint = addField(4, "LBL_Fingerprint");
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
        DateFormat dateFormat = DateFormat.getDateInstance();
        notBefore.setText(dateFormat.format(cert.getNotBefore()));
        notAfter.setText(dateFormat.format(cert.getNotAfter()));
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(cert.getEncoded());
            byte[] digest = md.digest();
            StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < digest.length; i++) {
                if (i > 0) {
                    buffer.append(':');
                }
                buffer.append(getHexDigit(((int)digest[i] & 0xf0) >> 4));
                buffer.append(getHexDigit((int)digest[i] & 0x0f));
            }
            fingerprint.setText(buffer.toString());
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Unexpected exception", ex);
        }
    }
    
    private static char getHexDigit(int nibble) {
        return (char)(nibble < 10 ? '0' + nibble : 'A' + nibble - 10);
    }
}
