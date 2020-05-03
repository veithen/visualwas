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
package com.ibm.websphere.logging;

import java.util.logging.Level;

/**
 * Replacement class for WebSphere's <code>WsLevel</code> class. It prevents the WebSphere
 * code from registering is own log levels, because this would cause a class loader leak
 * (see <a href="http://bugs.sun.com/view_bug.do?bug_id=6543126">JDK-6543126</a>).
 */
public class WsLevel extends Level {
    private static final long serialVersionUID = -8795434113718441359L;
    
    // FATAL is 1100; SEVERE is 1000
    public static final Level FATAL = Level.SEVERE;
    
    // AUDIT is 850; INFO is 800
    public static final Level AUDIT = Level.INFO;
    
    // DETAIL is 625; FINE is 500
    public static final Level DETAIL = Level.FINE;
    
    public static final Level[] LEVELS = { ALL, FINEST, FINER, FINE, DETAIL, CONFIG, INFO, AUDIT, WARNING, SEVERE, FATAL, OFF };
    public static final int[] LEVEL_VALUES;
    
    static {
        LEVEL_VALUES = new int[LEVELS.length];
        for (int i=0; i<LEVELS.length; i++) {
            LEVEL_VALUES[i] = LEVELS[i].intValue();
        }
    }
    
    // Actually never used; just make the compiler happy
    private WsLevel(String name, int value, String resourceBundleName) {
        super(name, value, resourceBundleName);
    }
    
    // TODO: implement parse method
}
