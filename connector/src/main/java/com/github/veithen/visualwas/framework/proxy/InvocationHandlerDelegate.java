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
package com.github.veithen.visualwas.framework.proxy;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

final class InvocationHandlerDelegate {
    private static final Log log = LogFactory.getLog(InvocationHandlerDelegate.class);

    private final Operation operation;
    private final InvocationStyle invocationStyle;

    InvocationHandlerDelegate(Operation operation, InvocationStyle invocationStyle) {
        this.operation = operation;
        this.invocationStyle = invocationStyle;
    }

    Object invoke(InvocationTarget target, Object[] args) throws Throwable {
        if (log.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder("Invoking operation ");
            builder.append(operation.getName());
            if (args != null && args.length > 0) {
                builder.append(" with arguments ");
                builder.append(Arrays.asList(args));
            }
            log.debug(builder.toString());
        }
        return invocationStyle.invoke(target, operation, args);
    }
}
