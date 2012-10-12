/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.esigate.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.cookie.Cookie;
import org.esigate.api.HttpResponse;

public class MockHttpResponse implements HttpResponse {
	private int statusCode = 200;
	private final HashMap<String, String> headers = new HashMap<String, String>();
	private ByteArrayOutputStream outputStream;

	public OutputStream getOutputStream() throws IOException {
		if (outputStream == null)
			outputStream = new ByteArrayOutputStream();
		return outputStream;
	}

	public void addCookie(Cookie cookie) {
		throw new RuntimeException("Method not implemented");
	}

	public void addHeader(String name, String value) {
		headers.put(name.toLowerCase(), value);
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatus(int sc) {
		this.statusCode = sc;
	}

	public boolean containsHeader(String string) {
		return headers.containsKey(string.toLowerCase());
	}

	public String getBodyAsString() throws IOException {
		if (outputStream != null) {
			byte[] byteArray = outputStream.toByteArray();
			if (containsHeader("Content-encoding")) {
				// Decompress
				if (!headers.get("content-encoding").equals("gzip"))
					throw new RuntimeException("unsupported content-encoding: " + headers.get("content-encoding"));
				ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
				GZIPInputStream gzis = new GZIPInputStream(bais);
				byteArray = IOUtils.toByteArray(gzis);
				gzis.close();
			}
			String charset = "ISO-8859-1";
			String contentType = headers.get("content-type");
			if (contentType != null && contentType.toLowerCase().contains("utf-8"))
				charset = "UTF-8";
			return new String(byteArray, charset);

		}
		return null;
	}

}
