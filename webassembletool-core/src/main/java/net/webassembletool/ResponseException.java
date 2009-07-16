package net.webassembletool;

/**
 * @author Francois-Xavier Bonnet
 *
 */
public class ResponseException extends RuntimeException {
	private static final long serialVersionUID = 3887951483830172984L;

	public ResponseException(String message, Throwable cause) {
		super(message, cause);
	}

}
