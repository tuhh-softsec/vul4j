package net.webassembletool;

/**
 * Root exception for WAT library
 * 
 * @author Stanislav Bernatskyi
 */
public abstract class RenderingException extends Exception {
    private static final long serialVersionUID = -7750554559914315960L;

    protected RenderingException() {
        // default constructor
    }

    protected RenderingException(String message) {
        super(message);
    }

    protected RenderingException(String message, Throwable cause) {
        super(message, cause);
    }

}
