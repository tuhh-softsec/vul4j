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

package org.esigate.resource;

import java.util.Properties;

import junit.framework.TestCase;

import org.apache.http.HttpEntityEnclosingRequest;
import org.esigate.Driver;
import org.esigate.HttpClientDriver;
import org.esigate.Parameters;
import org.esigate.http.ResourceUtils;
import org.esigate.test.TestUtils;

public class ResourceUtilsTest extends TestCase {

	public void testGetHttpUrlWithQueryString() throws Exception {
		Properties props = new Properties();
		props.put(Parameters.REMOTE_URL_BASE.name, "http://www.foo.com/");
		Driver driver = new HttpClientDriver("test", props);
		HttpEntityEnclosingRequest request = TestUtils.createRequest("http://bar.com");
		driver.initHttpRequestParams(request, null);
		assertEquals("http://www.foo.com/test", ResourceUtils.getHttpUrlWithQueryString("/test", request, true));
	}

	public void testGetHttpUrlWithQueryStringAbsoluteurl() throws Exception {
		Properties props = new Properties();
		props.put(Parameters.REMOTE_URL_BASE.name, "http://www.foo.com/");
		Driver driver = new HttpClientDriver("test", props);
		HttpEntityEnclosingRequest request = TestUtils.createRequest("http://bar.com");
		driver.initHttpRequestParams(request, null);
		assertEquals("http://www.bar.com/test", ResourceUtils.getHttpUrlWithQueryString("http://www.bar.com/test", request, true));
	}
}
