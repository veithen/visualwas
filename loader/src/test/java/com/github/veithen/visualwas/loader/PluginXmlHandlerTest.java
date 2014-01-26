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
package com.github.veithen.visualwas.loader;

import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import org.junit.Assert;
import org.junit.Test;

public class PluginXmlHandlerTest extends Assert {
    @Test
    public void test() throws Exception {
        PluginXmlHandler handler = new PluginXmlHandler();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.newSAXParser().parse(PluginXmlHandlerTest.class.getResource("plugin.xml").toString(), handler);
        List<String> serializables = handler.getSerializables();
        assertEquals(2, serializables.size());
        assertEquals("class1", serializables.get(0));
        assertEquals("class2", serializables.get(1));
    }
}
