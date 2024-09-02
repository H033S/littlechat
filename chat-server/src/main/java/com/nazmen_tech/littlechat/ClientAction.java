package com.nazmen_tech.littlechat;

import java.io.IOException;

@FunctionalInterface
public interface ClientAction {

    void perform() throws IOException;
}
