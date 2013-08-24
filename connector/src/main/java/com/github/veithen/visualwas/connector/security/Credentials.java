package com.github.veithen.visualwas.connector.security;

import java.net.HttpURLConnection;

import com.github.veithen.visualwas.connector.feature.Dependencies;

@Dependencies(SecurityFeature.class)
public interface Credentials {
    void configure(HttpURLConnection connection);
}
