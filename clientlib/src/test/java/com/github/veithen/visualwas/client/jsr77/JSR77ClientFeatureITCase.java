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
package com.github.veithen.visualwas.client.jsr77;

import static org.assertj.core.api.Assertions.assertThat;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.ObjectName;

import org.junit.jupiter.api.Test;

import com.github.veithen.visualwas.client.WebSphereITCase;
import com.github.veithen.visualwas.connector.feature.Feature;

public class JSR77ClientFeatureITCase extends WebSphereITCase {
    @Override
    protected Feature[] getFeatures() {
        return new Feature[] {JSR77ClientFeature.INSTANCE};
    }

    @Test
    @SuppressWarnings("ReturnValueIgnored")
    public void testGetStatsAttributeFromAllMBeans() throws Exception {
        int count = 0;
        for (ObjectName mbean : connector.queryNames(new ObjectName("WebSphere:*"), null)) {
            for (MBeanAttributeInfo attrInfo : connector.getMBeanInfo(mbean).getAttributes()) {
                if (attrInfo.getName().equals("stats")) {
                    try {
                        Object stats = connector.getAttribute(mbean, "stats");
                        if (stats != null) {
                            assertThat(stats).isInstanceOf(Class.forName(attrInfo.getType()));
                            // Check that toString doesn't trigger any exceptions.
                            stats.toString();
                            count++;
                        }
                    } catch (MBeanException ex) {
                        if (!ex.getMessage().contains("Target method not found")) {
                            throw ex;
                        }
                    }
                    break;
                }
            }
        }
        assertThat(count).isNotEqualTo(0);
    }
}
