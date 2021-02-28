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

import static org.xmlunit.assertj3.XmlAssert.assertThat;

import org.w3c.dom.Document;

final class Exchange {
    private final Document request;
    private final Response response;

    Exchange(Document request, Response response) {
        this.request = request;
        this.response = response;
    }

    void assertRequestEquals(Document test) {
        assertThat(request.getDocumentElement())
                .and(test.getDocumentElement())
                .ignoreWhitespace()
                .areSimilar();
    }

    Response getResponse() {
        return response;
    }
}
