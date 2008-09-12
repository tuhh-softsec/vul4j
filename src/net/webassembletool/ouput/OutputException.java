package net.webassembletool.ouput;

public class OutputException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public OutputException(String message) {
	super(message);
    }

    public OutputException(String message, Throwable cause) {
	super(message, cause);
    }

}
