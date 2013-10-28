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
