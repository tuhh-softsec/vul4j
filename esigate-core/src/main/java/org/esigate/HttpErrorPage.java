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
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.util.EntityUtils;
import org.esigate.http.HttpResponseUtils;

/**
 * Exception thrown when an error occurred retrieving a resource
 * 
 * @author Francois-Xavier Bonnet
 */
public class HttpErrorPage extends Exception {
	private static final long serialVersionUID = 1L;
	private final HttpResponse errorResponse;

	public HttpErrorPage(HttpResponse httpResponse) {
		super(httpResponse.getStatusLine().getStatusCode() + " " + httpResponse.getStatusLine().getReasonPhrase());
		StatusLine statusLine = httpResponse.getStatusLine();
		this.errorResponse = new BasicHttpResponse(statusLine);
		this.errorResponse.setHeaders(httpResponse.getAllHeaders());
		HttpEntity httpEntity = httpResponse.getEntity();
		if (httpEntity != null) {
			Header contentType = httpEntity.getContentType();
			Header contentEncoding = httpEntity.getContentEncoding();
			String content;
			try {
				content = EntityUtils.toString(httpEntity);
			} catch (IOException e) {
				StringWriter out = new StringWriter();
				e.printStackTrace(new PrintWriter(out));
				content = out.toString();
			}
			try {
				StringEntity stringEntity = new StringEntity(content);
				stringEntity.setContentType(contentType);
				stringEntity.setContentEncoding(contentEncoding);
				this.errorResponse.setEntity(stringEntity);
			} catch (UnsupportedEncodingException e) {
				// This will never happen as the problem would have been
				// detected before
				throw new RuntimeException(e);
			}
		}
	}

	public HttpErrorPage(int statusCode, String statusMessage, String content) {
		super(statusCode + " " + statusMessage);
		this.errorResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, statusCode, statusMessage));
		try {
			this.errorResponse.setEntity(new StringEntity(content, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// This should not happen as UTF-8 is always supported
			throw new RuntimeException(e);
		}
	}

	public HttpErrorPage(int statusCode, String statusMessage, Exception exception) {
		super(statusCode + " " + statusMessage, exception);
		this.errorResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, statusCode, statusMessage));
		StringWriter out = new StringWriter();
		exception.printStackTrace(new PrintWriter(out));
		String content = out.toString();
		try {
			this.errorResponse.setEntity(new StringEntity(content, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// This should not happen as UTF-8 is always supported
			throw new RuntimeException(e);
		}
	}

	public void render(Writer writer) throws IOException {
		writer.write(HttpResponseUtils.toString(errorResponse));
	}

	public void render(org.esigate.api.HttpResponse httpResponse) throws IOException {
		httpResponse.setStatus(errorResponse.getStatusLine().getStatusCode());
		// FIXME We should filter the headers and rewrite urls here
		for (Header header : errorResponse.getAllHeaders()) {
			String name = header.getName();
			String value = header.getValue();
			httpResponse.addHeader(name, value);
		}
		HttpEntity httpEntity = errorResponse.getEntity();
		if (httpEntity != null) {
			InputStream content = httpEntity.getContent();
			try {
				IOUtils.copy(content, httpResponse.getOutputStream());
			} finally {
				content.close();
			}
		}
	}

	public int getStatusCode() {
		return errorResponse.getStatusLine().getStatusCode();
	}

	public String getStatusMessage() {
		return errorResponse.getStatusLine().getReasonPhrase();
	}
}
