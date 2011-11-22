package org.esigate;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

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

	public HttpErrorPage(int statusCode, String statusMessage,
			String errorPageContent) {
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

	public void render(Writer writer) throws IOException {
		writer.write(errorPageContent);
	}
	
	public void render(HttpServletResponse response) throws IOException {
		try {
			response.setStatus(statusCode);
			Writer writer = response.getWriter();
			render(writer);
			// If we cannot render the Exception to browser, do our best to
			// render it in the log file
		} catch (IllegalStateException e) {
			throw new ResponseException(
					"Response already committed, unable to render exception to browser",
					this);
		} catch (IOException e) {
			throw new ResponseException(
					"Unable to render exception to browser", this);
		}

	}
}
