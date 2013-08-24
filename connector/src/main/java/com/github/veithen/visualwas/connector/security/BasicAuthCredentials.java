package com.github.veithen.visualwas.connector.security;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import org.apache.commons.codec.binary.Base64;

public final class BasicAuthCredentials implements Credentials {
    private final String username;
    private final String password;
    
    public BasicAuthCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public void configure(HttpURLConnection connection) {
        try {
            connection.setRequestProperty("Authorization", "Basic " + new String(Base64.encodeBase64((username + ":" + password).getBytes("utf-8")), "ascii"));
        } catch (UnsupportedEncodingException ex) {
            throw new Error("Unexpected exception", ex);
        }
    }
}
