package org.esigate.util;

import java.io.IOException;
import java.util.Collection;

import org.esigate.DriverConfiguration;
import org.esigate.HttpErrorPage;
import org.esigate.api.HttpStatusConstants;
import org.esigate.output.Output;
import org.esigate.output.StringOutput;
import org.esigate.resource.Resource;

/**
 * Rfc2616 caching implementation.
 * 
 * @author Francois-Xavier Bonnet
 * @author Nicolas Richeton
 * 
 */
public class Rfc2616 {
	private Rfc2616() {

	}

	public final static void renderResource(DriverConfiguration config, Resource resource, Output output) throws IOException, HttpErrorPage {
		if (resource.isError()) {
			String errorPageContent;
			StringOutput stringOutput = new StringOutput();
			resource.render(stringOutput);
			errorPageContent = stringOutput.toString();
			output.setStatusCode(resource.getStatusCode());
			output.setStatusMessage(resource.getStatusMessage());
			copyHeaders(config, resource, output);
			output.open();
			output.write(errorPageContent);
			output.close();
			throw new HttpErrorPage(resource.getStatusCode(), resource.getStatusMessage(), errorPageContent);
		} else if (HttpStatusConstants.SC_NOT_MODIFIED == resource.getStatusCode()) {
			output.setStatusCode(resource.getStatusCode());
			output.setStatusMessage(resource.getStatusMessage());
			copyHeaders(config, resource, output);
			output.open();
			output.getOutputStream();
			// No response body
			output.close();
		} else {
			resource.render(output);
		}
	}

	/** Copies end-to-end headers from a resource to an output. */
	public final static void copyHeaders(DriverConfiguration config, Resource resource, Output output) {
		for (String headerName : resource.getHeaderNames()) {
			if (config.isForwardedResponseHeader(headerName)) {
				Collection<String> values = resource.getHeaders(headerName);
				for (String value : values) {
					output.addHeader(headerName, value);
				}
			}
		}
	}

}
