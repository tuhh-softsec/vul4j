package net.webassembletool.http;

/**
 * Exception thrown when an unsupported HTTP method is called
 * 
 * @author François-Xavier Bonnet
 */
public class UnsupportedHttpMethodException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * @param message Error message
     */
    public UnsupportedHttpMethodException(String message) {
	super(message);
    }

}
