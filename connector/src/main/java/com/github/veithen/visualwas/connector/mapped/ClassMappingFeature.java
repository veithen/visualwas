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
        adapter.addMappedClasses(SOAPException.class);
    }
}
