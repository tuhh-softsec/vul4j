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

package org.esigate.http;

import java.io.IOException;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.esigate.Driver;
import org.esigate.Parameters;
import org.esigate.ResourceContext;
import org.esigate.ResourceFactory;
import org.esigate.output.StringOutput;
import org.esigate.resource.Resource;
import org.esigate.test.MockHttpRequest;
import org.esigate.test.MockHttpResponse;

public class HttpResourceFactoryTest extends TestCase {
	private MockHttpClient mockHttpClient;
	private Properties properties;
	private ResourceFactory resourceFactory;
	private Driver driver;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		properties = new Properties();
		mockHttpClient = new MockHttpClient();
		resourceFactory = null;
		driver = null;
	}

	private void createResourceFactory() {
		resourceFactory = ResourceFactoryCreator.create(properties, mockHttpClient);
		driver = new Driver("mock", properties, resourceFactory);
	}

	private HttpResponse createMockResponse(int statusCode, String entity) throws Exception {
		HttpResponse response = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), statusCode, "OK");
		HttpEntity httpEntity = new StringEntity(entity);
		response.setEntity(httpEntity);
		return response;
	}

	private HttpResponse createMockResponse(String entity) throws Exception {
		HttpResponse response = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), 200, "OK");
		HttpEntity httpEntity = new StringEntity(entity);
		response.setEntity(httpEntity);
		return response;
	}

	private boolean compare(HttpResponse response1, Resource response2) throws Exception, IOException {
		String entity1 = IOUtils.toString(response1.getEntity().getContent());
		StringOutput stringOutput = new StringOutput();
		response2.render(stringOutput);
		String entity2 = stringOutput.toString();
		return entity1.equals(entity2);
	}

	private Resource executeRequest() throws Exception {
		org.esigate.api.HttpRequest originalRequest = new MockHttpRequest();
		org.esigate.api.HttpResponse originalResponse = new MockHttpResponse();
		ResourceContext resourceContext = new ResourceContext(driver, "", null, originalRequest, originalResponse);
		return resourceFactory.getResource(resourceContext);
	}

	public void testCacheTtl() throws Exception {
		properties.put(Parameters.REMOTE_URL_BASE.name, "http://localhost");
		properties.put(Parameters.USE_CACHE.name, "true"); // Default value
		properties.put(Parameters.TTL.name, "1");
		createResourceFactory();
		// First request
		HttpResponse response = createMockResponse("0");
		response.setHeader("Cache-control", "no-cache");
		mockHttpClient.setResponse(response);
		Resource result = executeRequest();
		assertTrue("Response content should be '0'", compare(response, result));
		// Second request should use cache
		HttpResponse response1 = createMockResponse("1");
		response.setHeader("Cache-control", "no-cache");
		mockHttpClient.setResponse(response1);
		result = executeRequest();
		assertTrue("Response content should be unchanged as cache should be used.", compare(response, result));
		// Third request after cache has expired
		Thread.sleep(1000);
		result = executeRequest();
		assertTrue("Response should have been refreshed.", compare(response1, result));
	}

	public void testCacheTtlErrorPage() throws Exception {
		properties.put(Parameters.REMOTE_URL_BASE.name, "http://localhost");
		properties.put(Parameters.USE_CACHE.name, "true"); // Default value
		properties.put(Parameters.TTL.name, "1");
		createResourceFactory();
		// First request
		HttpResponse response = createMockResponse(404, "0");
		response.setHeader("Cache-control", "no-cache");
		mockHttpClient.setResponse(response);
		Resource result = executeRequest();
		assertTrue("Response content should be '0'", compare(response, result));
		// Second request should use cache even if first response was a 404
		HttpResponse response1 = createMockResponse("1");
		response.setHeader("Cache-control", "no-cache");
		mockHttpClient.setResponse(response1);
		result = executeRequest();
		assertTrue("Response content should be unchanged as cache should be used.", compare(response, result));
		// Third request after cache has expired
		Thread.sleep(1000);
		result = executeRequest();
		assertTrue("Response should have been refreshed.", compare(response1, result));
	}

	public void testCacheStaleIfError() throws Exception {
		properties.put(Parameters.REMOTE_URL_BASE.name, "http://localhost");
		properties.put(Parameters.USE_CACHE.name, "true"); // Default value
		properties.put(Parameters.STALE_IF_ERROR.name, "60");
		properties.put(Parameters.STALE_WHILE_REVALIDATE.name, "60");
		properties.put(Parameters.MIN_ASYNCHRONOUS_WORKERS.name, "1");
		properties.put(Parameters.MAX_ASYNCHRONOUS_WORKERS.name, "10");
		properties.put(Parameters.HEURISTIC_CACHING_ENABLED.name, "false");
		createResourceFactory();
		// First request
		HttpResponse response = createMockResponse("0");
		response.setHeader("Last-modified", "Fri, 20 May 2011 00:00:00 GMT"); // HttpClient should store in cache and send a conditional request next time
		response.setHeader("Cache-control", "max-age=0"); // HttpClient should store in cache and send a conditional request next time
		mockHttpClient.setResponse(response);
		Resource result = executeRequest();
		assertTrue("Response content should be '0'", compare(response, result));
		// Second request should use cache even if first response was a 404
		HttpResponse response1 = createMockResponse(500, "1");
		mockHttpClient.setResponse(response1);
		result = executeRequest();
		assertTrue("Response content should be unchanged as cache should be used on error.", compare(response, result));
		// Third request no more error but stale-while-refresh should trigger a background revalidation and serve the old version.
		HttpResponse response2 = createMockResponse(200, "2");
		mockHttpClient.setResponse(response2);
		result = executeRequest();
		assertTrue("Response should have been refreshed.", compare(response, result));
		// Wait until revalidation is complete
		Thread.sleep(100);
		// Fourth request after cache has been updated at last
		result = executeRequest();
		assertTrue("Response should have been refreshed.", compare(response2, result));
	}
}
