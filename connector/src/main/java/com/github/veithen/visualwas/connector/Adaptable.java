package com.github.veithen.visualwas.connector;

public interface Adaptable {
    <T> T getAdapter(Class<T> clazz);
}
