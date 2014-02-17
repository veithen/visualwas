/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2014 Andreas Veithen
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

import java.util.HashMap;
import java.util.Map;

final class ClassMapper {
    private final Map<String,String> replacementClasses = new HashMap<String,String>();
    private final Map<String,String> originalClasses = new HashMap<String,String>();
    
    void addMapping(String originalClass, String replacementClass) {
        replacementClasses.put(originalClass, replacementClass);
        originalClasses.put(replacementClass, originalClass);
    }
    
    private static String map(Map<String,String> map, String signature) {
        int dims = 0;
        while (signature.charAt(dims) == '[') {
            dims++;
        }
        if (dims == 0 || signature.charAt(dims) == 'L') {
            String className = dims == 0 ? signature : signature.substring(dims+1, signature.length()-1);
            String replacementClass = map.get(className);
            if (replacementClass != null) {
                return dims == 0 ? replacementClass : signature.substring(0, dims+1) + replacementClass + ";";
            }
        }
        return signature;
    }
    
    String toRemoteClass(String localClass) {
        return map(originalClasses, localClass);
    }
    
    String toLocalClass(String remoteClass) {
        return map(replacementClasses, remoteClass);
    }
}
