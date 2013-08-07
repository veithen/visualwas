package com.github.veithen.visualwas;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public abstract class SimpleDocumentListener implements DocumentListener {

    @Override
    public final void insertUpdate(DocumentEvent e) {
        updated();
    }

    @Override
    public final void removeUpdate(DocumentEvent e) {
        updated();
    }

    @Override
    public final void changedUpdate(DocumentEvent e) {
        updated();
    }

    protected abstract void updated();
}
