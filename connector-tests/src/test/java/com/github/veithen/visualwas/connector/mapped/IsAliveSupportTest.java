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
package com.github.veithen.visualwas.connector.mapped;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.github.veithen.visualwas.connector.Connector;
import com.github.veithen.visualwas.connector.transport.dummy.DummyTransport;
import com.github.veithen.visualwas.connector.transport.dummy.SequencedRequestMatcher;

public class IsAliveSupportTest {
    @Test
    public void test() throws Exception {
        DummyTransport transport = new DummyTransport(new SequencedRequestMatcher());
        transport.addExchanges(IsAliveSupportTest.class, "isAlive");
        Connector connector = transport.createConnector(ClassMappingFeature.INSTANCE);
        IsAliveSupport ias = connector.getAdapter(IsAliveSupport.class);
        assertThat(ias).isNotNull();
        Session session = ias.isAlive();
        assertThat(session).isNotNull();
    }
}
