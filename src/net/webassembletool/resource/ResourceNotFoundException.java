package net.webassembletool.resource;

/**
 * Exception to be thrown when a resource does not exist.
 * 
 * @author François-Xavier Bonnet
 * 
 */
public class ResourceNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;
    public ResourceNotFoundException(String relUrl) {
	super("Resource not found: " + relUrl);
    }
}
