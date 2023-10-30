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
package com.github.veithen.visualwas.mxbeans;

import java.util.HashMap;
import java.util.Map;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ReflectionException;

public class AccessControlProxy implements DynamicMBean {
    private final Map<String, String> attributeNameToGetterNameMap = new HashMap<String, String>();
    private final Map<String, String> attributeNameToSetterNameMap = new HashMap<String, String>();

    private final DynamicMBean target;
    private final String type;
    private final AccessChecker accessChecker;

    public AccessControlProxy(DynamicMBean target, String type, AccessChecker accessChecker) {
        this.target = target;
        this.type = type;
        this.accessChecker = accessChecker;
        for (MBeanAttributeInfo info : target.getMBeanInfo().getAttributes()) {
            String name = info.getName();
            attributeNameToGetterNameMap.put(name, info.isIs() ? "is" + name : "get" + name);
            attributeNameToSetterNameMap.put(name, "set" + name);
        }
    }

    private void checkAccess(String methodName) {
        // Allow access to getObjectName (defined by PlatformManagedObject) to anybody.
        if (!methodName.equals("getObjectName")) {
            accessChecker.checkAccess(type + "." + methodName);
        }
    }

    private void checkAttributeAccess(
            String attribute, Map<String, String> attributeNameToMethodNameMap) {
        String methodName = attributeNameToMethodNameMap.get(attribute);
        // If the attribute is unknown, let the target throw an appropriate exception
        if (methodName != null) {
            checkAccess(methodName);
        }
    }

    private void checkAttributeReadAccess(String attribute) {
        checkAttributeAccess(attribute, attributeNameToGetterNameMap);
    }

    private void checkAttributeWriteAccess(String attribute) {
        checkAttributeAccess(attribute, attributeNameToSetterNameMap);
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        return target.getMBeanInfo();
    }

    @Override
    public Object getAttribute(String attribute)
            throws AttributeNotFoundException, MBeanException, ReflectionException {
        checkAttributeReadAccess(attribute);
        return target.getAttribute(attribute);
    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        for (String attribute : attributes) {
            checkAttributeReadAccess(attribute);
        }
        return target.getAttributes(attributes);
    }

    @Override
    public void setAttribute(Attribute attribute)
            throws AttributeNotFoundException,
                    InvalidAttributeValueException,
                    MBeanException,
                    ReflectionException {
        checkAttributeWriteAccess(attribute.getName());
        target.setAttribute(attribute);
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        for (Object attribute : attributes) {
            checkAttributeWriteAccess(((Attribute) attribute).getName());
        }
        return target.setAttributes(attributes);
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature)
            throws MBeanException, ReflectionException {
        checkAccess(actionName);
        return target.invoke(actionName, params, signature);
    }
}
