package com.github.veithen.visualwas.connector;

import java.io.IOException;

public class ConnectorException extends IOException {
    private static final long serialVersionUID = -8573559165073929329L;

    public ConnectorException(String message, Throwable cause) {
        super(message, cause);
    }
}
