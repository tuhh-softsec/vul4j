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
package org.esigate.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.esigate.HttpErrorPage;
import org.esigate.ResourceContext;
import org.esigate.ResourceFactory;
import org.esigate.resource.Resource;

public class HttpResourceFactory implements ResourceFactory {
	private final HttpClient httpClient;

	public HttpResourceFactory(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public Resource getResource(ResourceContext resourceContext)
			throws HttpErrorPage {
		try {
			return new HttpResource(httpClient, resourceContext);
		} catch (IOException e) {
			StringWriter out = new StringWriter();
			e.printStackTrace(new PrintWriter(out));
			HttpErrorPage httpErrorPage = new HttpErrorPage(
					HttpStatus.SC_BAD_GATEWAY, e.getMessage(),
					out.toString());
			httpErrorPage.initCause(e);
			throw httpErrorPage;
		}
	}
}
