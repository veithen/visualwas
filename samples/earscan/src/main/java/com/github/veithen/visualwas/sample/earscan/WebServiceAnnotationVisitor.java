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
package com.github.veithen.visualwas.sample.earscan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

public class WebServiceAnnotationVisitor extends AnnotationVisitor {
    private final List<WebServiceImplementation> wsImplementations;
    private final String className;
    private final Map<String,Object> attributes = new HashMap<>();
    
    public WebServiceAnnotationVisitor(List<WebServiceImplementation> wsImplementations, String className) {
        super(Opcodes.ASM9);
        this.wsImplementations = wsImplementations;
        this.className = className;
    }
    
    public void visit(String name, Object value) {
        attributes.put(name, value);
    }

    public void visitEnd() {
        wsImplementations.add(new WebServiceImplementation(className,
                (String)attributes.get("endpointInterface"),
                (String)attributes.get("wsdlLocation"),
                (String)attributes.get("targetNamespace"),
                (String)attributes.get("serviceName"),
                (String)attributes.get("portName")));
    }
}
