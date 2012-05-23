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

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

/**
 * Mock implementation for HttpClient for unit tests.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class MockHttpClient implements HttpClient {
	private final HttpParams params = new BasicHttpParams();
	private HttpResponse response;
	private long sleep = 0l;

	private void sleep() {
		if (sleep > 0)
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
	}

	public HttpParams getParams() {
		return params;
	}

	public ClientConnectionManager getConnectionManager() {
		throw new RuntimeException("Method not implemented");
	}

	public HttpResponse execute(HttpUriRequest request) throws IOException, ClientProtocolException {
		sleep();
		return response;
	}

	public HttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException, ClientProtocolException {
		sleep();
		return response;
	}

	public HttpResponse execute(HttpHost target, HttpRequest request) throws IOException, ClientProtocolException {
		sleep();
		return response;
	}

	public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException {
		sleep();
		return response;
	}

	public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
		sleep();
		return responseHandler.handleResponse(response);
	}

	public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
		sleep();
		return responseHandler.handleResponse(response);
	}

	public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
		sleep();
		return responseHandler.handleResponse(response);
	}

	public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
		sleep();
		return responseHandler.handleResponse(response);
	}

	public HttpResponse getResponse() {
		return response;
	}

	public void setResponse(HttpResponse response) {
		this.response = response;
	}

	public long getSleep() {
		return sleep;
	}

	public void setSleep(long sleep) {
		this.sleep = sleep;
	}

}
