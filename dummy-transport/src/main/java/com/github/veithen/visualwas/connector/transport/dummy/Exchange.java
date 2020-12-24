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
package com.github.veithen.visualwas.connector.transport.dummy;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

final class Exchange {
    private final Document request;
    private final Response response;

    Exchange(Document request, Response response) {
        this.request = request;
        this.response = response;
    }

    Diff diff(Document test) {
        boolean ignoreWhitespace = XMLUnit.getIgnoreWhitespace();
        boolean ignoreAttributeOrder = XMLUnit.getIgnoreAttributeOrder();
        try {
            XMLUnit.setIgnoreWhitespace(true);
            XMLUnit.setIgnoreAttributeOrder(true);
            Diff diff = XMLUnit.compareXML(request, test);
            diff.overrideDifferenceListener(
                    new DifferenceListener() {
                        @Override
                        public int differenceFound(Difference difference) {
                            if (difference.getId() == DifferenceConstants.NAMESPACE_PREFIX_ID) {
                                return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
                            } else {
                                return RETURN_ACCEPT_DIFFERENCE;
                            }
                        }

                        @Override
                        public void skippedComparison(Node control, Node test) {}
                    });
            return diff;
        } finally {
            XMLUnit.setIgnoreWhitespace(ignoreWhitespace);
            XMLUnit.setIgnoreAttributeOrder(ignoreAttributeOrder);
        }
    }

    Response getResponse() {
        return response;
    }
}
