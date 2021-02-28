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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ConfigDataIdTest {
    @Test
    public void testToString() {
        assertThat(
                        new ConfigDataId(
                                        "cells/phobosNode03Cell/nodes/phobosNode03/servers/server1",
                                        "server.xml#ThreadPool_1183121908657")
                                .toString())
                .isEqualTo(
                        "cells/phobosNode03Cell/nodes/phobosNode03/servers/server1|server.xml#ThreadPool_1183121908657");
    }

    @Test
    public void testHashCode() {
        assertThat(
                        new ConfigDataId(
                                        "cells/phobosNode03Cell/nodes/phobosNode03/servers/server1",
                                        "server.xml#ThreadPool_1183121908657")
                                .hashCode())
                .isEqualTo(-1121788133);
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
        assertThat(id1.equals(id2)).isTrue();
        assertThat(id1.equals(id3)).isFalse();
    }
}
