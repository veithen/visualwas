package com.github.veithen.visualwas.connector.security;

import com.github.veithen.visualwas.connector.feature.Configurator;
import com.github.veithen.visualwas.connector.feature.Feature;

public final class SecurityFeature implements Feature {
    public static final SecurityFeature INSTANCE = new SecurityFeature();
    
    private SecurityFeature() {}

    @Override
    public void configureConnector(Configurator configurator) {
        configurator.addInterceptor(SecurityInterceptor.INSTANCE);
    }
}
