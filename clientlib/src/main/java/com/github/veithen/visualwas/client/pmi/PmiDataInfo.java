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
package com.github.veithen.visualwas.client.pmi;

import java.io.Serializable;
import java.util.ArrayList;

import com.github.veithen.visualwas.connector.mapped.MappedClass;

@MappedClass("com.ibm.websphere.pmi.PmiDataInfo")
public final class PmiDataInfo implements Serializable {
    private static final long serialVersionUID = -1609400918066043034L;

    private int id;
    private String name;
    private int type;
    private String description;
    private String category;
    private String unit;
    private int level;
    private boolean resettable;
    private boolean aggregatable;
    private boolean zosAggregatable;
    private boolean onRequest;
    private String statSet;
    private String platform;
    private String submoduleName;
    private String participation;
    private String comment;
    private ArrayList dependencyList;
    
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
