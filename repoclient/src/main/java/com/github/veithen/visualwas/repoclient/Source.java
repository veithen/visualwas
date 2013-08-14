package com.github.veithen.visualwas.repoclient;

import java.io.IOException;
import java.io.InputStream;

public interface Source {
    InputStream getInputStream() throws IOException;
}
