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
package com.github.veithen.visualwas.loader;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

final class PluginXmlHandler extends DefaultHandler {
    private enum State {
        DOCUMENT,
        PLUGIN,
        EXTENSION,
        SERIALIZABLE
    }

    private State state = State.DOCUMENT;
    private int skipDepth = 0;
    private final List<String> serializables = new ArrayList<>();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        if (skipDepth > 0) {
            skipDepth++;
        } else {
            switch (state) {
                case DOCUMENT:
                    state = State.PLUGIN;
                    break;
                case PLUGIN:
                    if (localName.equals("extension")
                            && "com.ibm.wsspi.extension.serializable"
                                    .equals(attributes.getValue("", "point"))) {
                        state = State.EXTENSION;
                    } else {
                        skipDepth++;
                    }
                    break;
                case EXTENSION:
                    if (localName.equals("serializable")) {
                        String className = attributes.getValue("", "class");
                        if (className != null) {
                            serializables.add(className);
                        }
                        state = State.SERIALIZABLE;
                    } else {
                        skipDepth++;
                    }
                    break;
                case SERIALIZABLE:
                    skipDepth++;
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (skipDepth > 0) {
            skipDepth--;
        } else {
            switch (state) {
                case DOCUMENT:
                    throw new IllegalStateException();
                case PLUGIN:
                    state = State.DOCUMENT;
                    break;
                case EXTENSION:
                    state = State.PLUGIN;
                    break;
                case SERIALIZABLE:
                    state = State.EXTENSION;
                    break;
            }
        }
    }

    List<String> getSerializables() {
        return serializables;
    }
}
