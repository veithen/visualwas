package com.github.veithen.visualwas.jmx.soap;

import java.io.IOException;

public class MissingClassException extends IOException {
    private static final long serialVersionUID = -3226581196699961023L;

    public MissingClassException(ClassNotFoundException ex) {
        super(ex.getMessage());
        setStackTrace(ex.getStackTrace());
    }
}
