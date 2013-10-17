package com.github.veithen.visualwas.connector.feature;

import com.github.veithen.visualwas.connector.Connector;

/**
 * Listener interface for receiving notification that {@link Connector#close()} has been called. To
 * receive this kind of notification, implement this interface on the adapter returned by the
 * {@link AdapterFactory} registered using
 * {@link Configurator#registerAdminServiceAdapter(Class, AdapterFactory)}.
 */
public interface CloseListener {
    /**
     * Inform the listener that {@link Connector#close()} has been called.
     */
    void closing();
}
