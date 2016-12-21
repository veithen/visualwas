/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2016 Andreas Veithen
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
package com.github.veithen.visualwas.connector;

import com.github.veithen.visualwas.connector.feature.CloseListener;
import com.github.veithen.visualwas.connector.feature.Feature;
import com.github.veithen.visualwas.framework.Adaptable;

public interface Connector extends AdminService, Adaptable {
    /**
     * Close this connector. Note that the underlying protocol (SOAP over HTTP) is connection-less,
     * i.e. there is no persistent TCP connection to the server that remains established over the
     * entire lifecycle of the connector. The purpose of this method is to allow {@link Feature
     * features} to perform cleanup and to release resources linked to the connector instance (such
     * as stopping threads).
     * 
     * @see CloseListener
     */
    void close();
}
