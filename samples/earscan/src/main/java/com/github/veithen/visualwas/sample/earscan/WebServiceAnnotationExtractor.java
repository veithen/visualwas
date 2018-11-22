package com.github.veithen.visualwas.sample.earscan;
/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2018 Andreas Veithen
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
import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class WebServiceAnnotationExtractor implements ClassVisitor {
    private final List<WebServiceImplementation> wsImplementations;
    private boolean isImplementation;
    private String className;

    public WebServiceAnnotationExtractor(List<WebServiceImplementation> wsImplementations) {
        this.wsImplementations = wsImplementations;
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        isImplementation = (access & (Opcodes.ACC_INTERFACE | Opcodes.ACC_ABSTRACT)) == 0;
        className = name.replace('/', '.');
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return isImplementation && desc.equals("Ljavax/jws/WebService;") ? new WebServiceAnnotationVisitor(wsImplementations, className) : null;
    }

    public void visitAttribute(Attribute arg0) {
        // TODO Auto-generated method stub
        
    }

    public void visitEnd() {
        // TODO Auto-generated method stub
        
    }

    public FieldVisitor visitField(int arg0, String arg1, String arg2,
            String arg3, Object arg4) {
        // TODO Auto-generated method stub
        return null;
    }

    public void visitInnerClass(String arg0, String arg1, String arg2, int arg3) {
        // TODO Auto-generated method stub
        
    }

    public MethodVisitor visitMethod(int arg0, String arg1, String arg2,
            String arg3, String[] arg4) {
        // TODO Auto-generated method stub
        return null;
    }

    public void visitOuterClass(String arg0, String arg1, String arg2) {
        // TODO Auto-generated method stub
        
    }

    public void visitSource(String arg0, String arg1) {
        // TODO Auto-generated method stub
        
    }
}
