package net.webassembletool.ouput;

public class OutputException extends RuntimeException {
    public OutputException(String message) {
	super(message);
    }

    public OutputException(String message, Throwable cause) {
	super(message, cause);
    }

}
