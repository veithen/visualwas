package com.github.veithen.visualwas.connector.altclasses;

import com.github.veithen.visualwas.connector.feature.Configurator;
import com.github.veithen.visualwas.connector.feature.Feature;

public final class AlternateClassesFeature implements Feature {
    public static final AlternateClassesFeature INSTANCE = new AlternateClassesFeature();
    
    private AlternateClassesFeature() {}
    
    @Override
    public void configureConnector(Configurator configurator) {
        ClassMapper classMapper = new ClassMapper();
        AlternateClassesConfigurator adapter = new AlternateClassesConfiguratorImpl(classMapper);
        configurator.registerConfiguratorAdapter(AlternateClassesConfigurator.class, adapter);
        configurator.setSerializer(new SerializerImpl(classMapper));
        adapter.addAlternateClasses(SOAPException.class);
    }
}
