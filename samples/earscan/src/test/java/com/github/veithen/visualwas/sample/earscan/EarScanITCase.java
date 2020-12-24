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
package com.github.veithen.visualwas.sample.earscan;

import java.net.InetSocketAddress;

import org.junit.Test;

import com.github.veithen.visualwas.client.repository.PortMapper;
import com.github.veithen.visualwas.connector.Attributes;
import com.github.veithen.visualwas.connector.security.BasicAuthCredentials;
import com.github.veithen.visualwas.connector.security.Credentials;

public class EarScanITCase {
    @Test
    public void test() throws Exception {
        int soapPort = Integer.getInteger("was.soapPort");
        int adminPort = Integer.getInteger("was.adminPort");
        Attributes attributes =
                Attributes.builder()
                        .set(Credentials.class, new BasicAuthCredentials("operator", "changeme"))
                        .set(
                                PortMapper.class,
                                address -> new InetSocketAddress("localhost", adminPort))
                        .build();
        Main.run("localhost", soapPort, attributes);
    }
}
