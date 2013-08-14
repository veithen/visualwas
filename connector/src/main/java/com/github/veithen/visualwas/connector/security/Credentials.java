package com.github.veithen.visualwas.connector.security;

import java.net.HttpURLConnection;

import com.github.veithen.visualwas.connector.Interceptor;

public interface Credentials {
    Interceptor createInterceptor();
    void configure(HttpURLConnection connection);
}
