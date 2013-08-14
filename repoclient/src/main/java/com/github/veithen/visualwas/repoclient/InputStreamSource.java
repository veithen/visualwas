package com.github.veithen.visualwas.repoclient;

import java.io.InputStream;

public class InputStreamSource implements Source {
    private final InputStream inputStream;

    public InputStreamSource(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }
}
