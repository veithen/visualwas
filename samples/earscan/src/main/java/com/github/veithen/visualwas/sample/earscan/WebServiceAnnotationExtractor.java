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

import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public class WebServiceAnnotationExtractor extends ClassVisitor {
    private final List<WebServiceImplementation> wsImplementations;
    private boolean isImplementation;
    private String className;

    public WebServiceAnnotationExtractor(List<WebServiceImplementation> wsImplementations) {
        super(Opcodes.ASM9);
        this.wsImplementations = wsImplementations;
    }

    @Override
    public void visit(
            int version,
            int access,
            String name,
            String signature,
            String superName,
            String[] interfaces) {
        isImplementation = (access & (Opcodes.ACC_INTERFACE | Opcodes.ACC_ABSTRACT)) == 0;
        className = name.replace('/', '.');
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return isImplementation && desc.equals("Ljavax/jws/WebService;")
                ? new WebServiceAnnotationVisitor(wsImplementations, className)
                : null;
    }
}
