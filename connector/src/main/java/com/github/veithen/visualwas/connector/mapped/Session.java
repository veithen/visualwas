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
package com.github.veithen.visualwas.connector.mapped;

import java.io.Serializable;

@MappedClass("com.ibm.websphere.management.Session")
public class Session implements Serializable {
    private static final long serialVersionUID = 2853465199951878373L;

    private long id;
    private String userName;
    private boolean shareWorkspace;
    
    public Session(long id, String userName, boolean shareWorkspace) {
        this.id = id;
        this.userName = userName;
        this.shareWorkspace = shareWorkspace;
    }
    
    public Session(String userName, boolean shareWorkspace) {
        this(System.currentTimeMillis(), userName, shareWorkspace);
    }
}
