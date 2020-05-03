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
package com.github.veithen.visualwas.connector.transport;

import java.net.MalformedURLException;
import java.net.URL;

public final class Endpoint {
    private final String host;
    private final int port;
    private final boolean securityEnabled;
    
    public Endpoint(String host, int port, boolean securityEnabled) {
        this.host = host;
        this.port = port;
        this.securityEnabled = securityEnabled;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
    
    public boolean isSecurityEnabled() {
        return securityEnabled;
    }

    public URL createURL() {
        try {
            return new URL(securityEnabled ? "https" : "http", host, port, "/");
        } catch (MalformedURLException ex) {
            throw new Error("Unexpected exception", ex);
        }
    }
}
