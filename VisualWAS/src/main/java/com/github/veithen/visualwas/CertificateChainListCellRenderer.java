package com.github.veithen.visualwas;

import java.awt.Component;
import java.security.cert.X509Certificate;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

final class CertificateChainListCellRenderer extends JLabel implements ListCellRenderer<X509Certificate> {
    private static ImageIcon icon = new ImageIcon(CertificateChainListCellRenderer.class.getResource("certificate.png"));
    
    CertificateChainListCellRenderer() {
        setIcon(icon);
        setOpaque(true);
    }
    
    public Component getListCellRendererComponent(JList<? extends X509Certificate> list,
            X509Certificate value, int index, boolean isSelected, boolean cellHasFocus) {
        setText(value.getSubjectDN().getName());
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setBorder(BorderFactory.createEmptyBorder(0, 10*index, 0, 0));
        return this;
    }
}
