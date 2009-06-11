package net.webassembletool.output;

public class OutputException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public OutputException(String message, Throwable cause) {
        super(message, cause);
    }

    public OutputException(Throwable cause) {
        super(cause);
    }

}
