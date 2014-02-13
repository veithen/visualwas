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
package com.github.veithen.visualwas.connector.mapped;

import com.github.veithen.visualwas.connector.feature.Configurator;
import com.github.veithen.visualwas.connector.feature.Feature;

public final class ClassMappingFeature implements Feature {
    public static final ClassMappingFeature INSTANCE = new ClassMappingFeature();
    
    private ClassMappingFeature() {}
    
    @Override
    public void configureConnector(Configurator configurator) {
        ClassMapper classMapper = new ClassMapper();
        ClassMappingConfigurator adapter = new ClassMappingConfiguratorImpl(classMapper);
        configurator.registerConfiguratorAdapter(ClassMappingConfigurator.class, adapter);
        configurator.setSerializer(new SerializerImpl(classMapper));
        configurator.addInvocationInterceptor(new InvocationInterceptor(classMapper));
        adapter.addMappedClasses(SOAPException.class);
    }
}
