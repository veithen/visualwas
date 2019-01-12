/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2019 Andreas Veithen
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

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

import javax.management.JMRuntimeException;
import javax.management.ObjectName;

import org.junit.Test;

import com.github.veithen.visualwas.connector.ConnectorException;
import com.github.veithen.visualwas.connector.WebSphereITCase;
import com.github.veithen.visualwas.connector.feature.Feature;

public class InvalidCredentialsITCase extends WebSphereITCase {
    @Override
    protected String getPassword() {
        return "invalid";
    }

    @Override
    protected Feature[] getFeatures() {
        return new Feature[] { ClassMappingFeature.INSTANCE };
    }

    @Test
    public void testInvalidCredentials() throws Exception {
        try {
            ObjectName serverMBean = connector.getServerMBean();
            connector.getAttribute(serverMBean, "pid");
            fail("Expected exception");
        } catch (JMRuntimeException ex) {
            // Older WebSphere versions produce a proper exception.
            assertThat(ex.getMessage()).contains("ADMN0022E");
        } catch (ConnectorException ex) {
            // Newer versions trigger a SOAPException.
            assertThat(ex.getCause()).isInstanceOf(SOAPException.class);
        }
    }
}
