package net.webassembletool;

import java.io.IOException;

public class ConfigurationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(IOException e) {
        super(e);
    }

}
