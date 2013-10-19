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
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Properties;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.esigate.test.http.HttpResponseBuilder;

public class MockDriver extends Driver {
	private final HashMap<String, String> resources = new HashMap<String, String>();

	private final static Properties getDefaultProperties() {
		Properties defaultProperties = new Properties();
		defaultProperties.put(Parameters.REMOTE_URL_BASE.name, "http://localhost");
		return defaultProperties;
	}

	public MockDriver(String name) {
		this(name, getDefaultProperties());
	}

	public MockDriver() {
		this("mock");
	}

	public MockDriver(String name, Properties props) {
		super(name, props);
		DriverFactory.put(name, this);
	}

	public void addResource(String relUrl, String content) throws IOException {
		resources.put(relUrl, content);
	}

	@Override
	public HttpResponse getResource(String relUrl, HttpEntityEnclosingRequest httpRequest) throws HttpErrorPage {
		String result = resources.get(relUrl);
		
		if (result == null) {
			throw new HttpErrorPage(404, "Not found", "The page: " + relUrl + " does not exist");
		}
		try {
			return new HttpResponseBuilder().status(200).reason("OK").entity(result).build();
		} catch (UnsupportedEncodingException e) {
			throw new HttpErrorPage(500,e.toString(),e.toString());
		}
	}

}
