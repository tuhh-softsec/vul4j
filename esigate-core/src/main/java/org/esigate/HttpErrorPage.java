package org.esigate;

import java.io.IOException;
import java.io.Writer;

/**
 * Exception thrown when an error occurred retrieving a resource
 * 
 * @author Francois-Xavier Bonnet
 */
public class HttpErrorPage extends Exception {
	private static final long serialVersionUID = 1L;
	private final int statusCode;
	private final String statusMessage;
	private final String errorPageContent;

	public HttpErrorPage(int statusCode, String statusMessage, String errorPageContent) {
		super(statusCode + " " + statusMessage);
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
		this.errorPageContent = errorPageContent;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public String getErrorPageContent() {
		return errorPageContent;
	}

	public void render(Writer writer) throws IOException {
		writer.write(errorPageContent);
	}
}
