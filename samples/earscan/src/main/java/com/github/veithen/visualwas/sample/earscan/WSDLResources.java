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
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.wsdl.xml.WSDLLocator;

import org.xml.sax.InputSource;

public class WSDLResources {
    class WSDLLocatorImpl implements WSDLLocator {
        private final String name;
        private String latestImport;
        
        WSDLLocatorImpl(String name) {
            this.name = name;
        }
        
        private InputSource getInputSource(String name) {
            byte[] content = resources.get(name);
            if (content == null) {
                throw new WSDLNotFoundException(name);
            }
            return new InputSource(new ByteArrayInputStream(content));
        }
        
        public InputSource getBaseInputSource() {
            return getInputSource(name);
        }

        public String getBaseURI() {
            return name;
        }

        public InputSource getImportInputSource(String parentLocation, String importLocation) {
            try {
                latestImport = new URI("module", null, "/" + parentLocation, null).resolve(importLocation).getPath().substring(1);
            } catch (URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
            return getInputSource(latestImport);
        }

        public String getLatestImportURI() {
            return latestImport;
        }
        
        public void close() {
        }
    }
    
    final Map<String,byte[]> resources = new HashMap<>();
    
    public void add(String name, byte[] content) {
        resources.put(name, content);
    }
    
    public WSDLLocator getWsdl(String name) {
        return new WSDLLocatorImpl(name);
    }
}
