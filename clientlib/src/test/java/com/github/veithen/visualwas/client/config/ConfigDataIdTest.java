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
package com.github.veithen.visualwas.client.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ConfigDataIdTest {
    @Test
    public void testToString() {
        assertEquals(
                "cells/phobosNode03Cell/nodes/phobosNode03/servers/server1|server.xml#ThreadPool_1183121908657",
                new ConfigDataId(
                                "cells/phobosNode03Cell/nodes/phobosNode03/servers/server1",
                                "server.xml#ThreadPool_1183121908657")
                        .toString());
    }

    @Test
    public void testHashCode() {
        assertEquals(
                -1121788133,
                new ConfigDataId(
                                "cells/phobosNode03Cell/nodes/phobosNode03/servers/server1",
                                "server.xml#ThreadPool_1183121908657")
                        .hashCode());
    }

    @Test
    public void testEquals() {
        ConfigDataId id1 =
                new ConfigDataId(
                        "cells/phobosNode03Cell/nodes/phobosNode03/servers/server1",
                        "server.xml#ThreadPool_1183121908657");
        ConfigDataId id2 =
                new ConfigDataId(
                        "cells/phobosNode03Cell/nodes/phobosNode03/servers/server1",
                        "server.xml#ThreadPool_1183121908657");
        ConfigDataId id3 =
                new ConfigDataId(
                        "cells/phobosNode03Cell/nodes/phobosNode03/servers/server1",
                        "server.xml#someOtherId");
        assertTrue(id1.equals(id2));
        assertFalse(id1.equals(id3));
    }
}
