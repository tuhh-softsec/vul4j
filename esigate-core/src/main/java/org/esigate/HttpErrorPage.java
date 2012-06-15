/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.esigate;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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

	public HttpErrorPage(int statusCode, String statusMessage, Exception exception) {
		super(statusCode + " " + statusMessage);
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
		StringWriter out = new StringWriter();
		exception.printStackTrace(new PrintWriter(out));
		this.errorPageContent = out.toString();
		this.initCause(exception);
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
