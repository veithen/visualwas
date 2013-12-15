/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 Andreas Veithen
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
