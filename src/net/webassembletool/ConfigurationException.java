package net.webassembletool;

import java.io.IOException;

public class ConfigurationException extends RuntimeException {
    public ConfigurationException(String message) {
	super(message);
    }

    public ConfigurationException(IOException e) {
	super(e);
    }

}
