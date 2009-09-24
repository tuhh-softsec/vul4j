package net.webassembletool.xml;


/**
 * Indicates that processing during rendering failed due to some reason
 * 
 * @author Stanislav Bernatskyi
 */
public class ProcessingFailedException extends RuntimeException {
    private static final long serialVersionUID = -2714936241490746932L;

    public ProcessingFailedException(String message, Throwable cause) {
        super(message, cause);
    }

}
