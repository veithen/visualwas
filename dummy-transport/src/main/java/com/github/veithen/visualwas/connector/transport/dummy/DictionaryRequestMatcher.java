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
package com.github.veithen.visualwas.connector.transport.dummy;

import static org.junit.Assert.fail;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;

public final class DictionaryRequestMatcher extends RequestMatcher {
    private final List<Exchange> exchanges = new ArrayList<>();

    @Override
    void add(Exchange exchange) {
        exchanges.add(exchange);
    }

    @Override
    protected URL match(Document request) {
        for (Exchange exchange : exchanges) {
            if (exchange.diff(request).similar()) {
                return exchange.getResponse();
            }
        }
        fail("No matching exchange found");
        return null; // Make compiler happy
    }
}
