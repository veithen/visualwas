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
package com.github.veithen.visualwas.connector.transport.dummy;

import java.net.URL;
import java.util.Deque;
import java.util.LinkedList;

import org.custommonkey.xmlunit.XMLAssert;
import org.w3c.dom.Document;

/**
 * {@link RequestMatcher} that assumes that requests are produced in the order in which they
 * are added using {@link DummyTransport#addExchange(URL, URL)}.
 */
public final class SequencedRequestMatcher extends RequestMatcher {
    private final Deque<Exchange> sequence = new LinkedList<>();
    
    @Override
    void add(Exchange exchange) {
        sequence.addLast(exchange);
    }

    @Override
    Response match(Document request) {
        if (sequence.isEmpty()) {
            throw new IllegalStateException();
        }
        Exchange exchange = sequence.removeFirst();
        XMLAssert.assertXMLEqual(exchange.diff(request), true);
        return exchange.getResponse();
    }
}
