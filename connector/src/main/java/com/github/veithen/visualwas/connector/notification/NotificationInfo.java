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
package com.github.veithen.visualwas.connector.notification;

import java.io.Serializable;

import javax.management.NotificationFilter;
import javax.management.ObjectName;

import com.github.veithen.visualwas.connector.mapped.MappedClass;

@MappedClass("com.ibm.ws.management.event.NotificationInfo")
final class NotificationInfo implements Serializable {
    private static final long serialVersionUID = 6448465470064588827L;

    private final ObjectName name;
    private final NotificationFilter filter;

    NotificationInfo(ObjectName name, NotificationFilter filter) {
        this.name = name;
        this.filter = filter;
    }

    ObjectName getName() {
        return name;
    }

    NotificationFilter getFilter() {
        return filter;
    }
}
