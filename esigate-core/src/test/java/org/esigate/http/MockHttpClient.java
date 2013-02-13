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
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.SSLSession;

import org.apache.http.HttpClientConnection;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestExecutor;

/**
 * Mock implementation for HttpClient for unit tests. The request passes all the
 * usual processing of the HttpClient except that it is not sent to the network.
 * Instead, the fake response that has been set by calling
 * setResponse(HttpResponse) is returned. The real request sent can be retrieved
 * with method getSentRequest().
 * 
 * @author Francois-Xavier Bonnet
 * @author Nicolas Richeton
 */
public class MockHttpClient extends DefaultHttpClient {
	private HttpResponse response;
	private HttpRequest sentRequest;
	private long sleep = 0l;
	private HttpRequestExecutor httpResponseExecutor;
	private final AtomicInteger openConnections = new AtomicInteger(0);

	private void sleep() {
		if (sleep > 0)
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
	}

	@Override
	protected HttpRequestExecutor createRequestExecutor() {

		// Let httpResponseExecutor handle the response.
		if (httpResponseExecutor != null)
			return httpResponseExecutor;

		// If no executor, respond with request.
		return new HttpRequestExecutor() {

			@Override
			public HttpResponse execute(HttpRequest request, HttpClientConnection conn, HttpContext context) throws IOException, HttpException {
				if (response == null)
					throw new RuntimeException("Mock response was not set");
				sleep();
				// Store the request that has been sent with all computed
				// headers.
				sentRequest = request;
				return response;
			}
		};
	}

	@Override
	protected ClientConnectionManager createClientConnectionManager() {
		return new ClientConnectionManager() {

			public void shutdown() {
			}

			public ClientConnectionRequest requestConnection(final HttpRoute route, Object state) {
				return new ClientConnectionRequest() {

					public ManagedClientConnection getConnection(long timeout, TimeUnit tunit) throws InterruptedException, ConnectionPoolTimeoutException {
						return new ManagedClientConnection() {
							private boolean open = false;

							public void releaseConnection() throws IOException {
								if (open) {
									openConnections.decrementAndGet();
									open = false;
								}
							}

							public void abortConnection() throws IOException {
								releaseConnection();
							}

							public int getRemotePort() {
								return 80;
							}

							public InetAddress getRemoteAddress() {
								throw new RuntimeException("Method not implemented");
							}

							public int getLocalPort() {
								throw new RuntimeException("Method not implemented");
							}

							public InetAddress getLocalAddress() {
								throw new RuntimeException("Method not implemented");
							}

							public void shutdown() throws IOException {
								releaseConnection();
							}

							public void setSocketTimeout(int timeout) {
							}

							public boolean isStale() {
								return false;
							}

							public boolean isOpen() {
								return open;
							}

							public int getSocketTimeout() {
								return 100;
							}

							public HttpConnectionMetrics getMetrics() {
								throw new RuntimeException("Method not implemented");
							}

							public void close() throws IOException {
								releaseConnection();
							}

							public void sendRequestHeader(HttpRequest request) throws HttpException, IOException {
							}

							public void sendRequestEntity(HttpEntityEnclosingRequest request) throws HttpException, IOException {
							}

							public HttpResponse receiveResponseHeader() throws HttpException, IOException {
								throw new RuntimeException("Method not implemented");
							}

							public void receiveResponseEntity(HttpResponse response) throws HttpException, IOException {
								throw new RuntimeException("Method not implemented");
							}

							public boolean isResponseAvailable(int timeout) throws IOException {
								throw new RuntimeException("Method not implemented");
							}

							public void flush() throws IOException {
							}

							public void unmarkReusable() {
							}

							public void tunnelTarget(boolean secure, HttpParams params) throws IOException {
								throw new RuntimeException("Method not implemented");
							}

							public void tunnelProxy(HttpHost next, boolean secure, HttpParams params) throws IOException {
								throw new RuntimeException("Method not implemented");
							}

							public void setState(Object state) {
								throw new RuntimeException("Method not implemented");
							}

							public void setIdleDuration(long duration, TimeUnit unit) {
								// Nothing to do
							}

							public void open(HttpRoute route, HttpContext context, HttpParams params) throws IOException {
								if (!open) {
									openConnections.incrementAndGet();
									open = true;
								} else {
									throw new IllegalStateException("Connection is already open");
								}
							}

							public void markReusable() {
								// Nothing to do
							}

							public void layerProtocol(HttpContext context, HttpParams params) throws IOException {
								throw new RuntimeException("Method not implemented");
							}

							public boolean isSecure() {
								return false;
							}

							public boolean isMarkedReusable() {
								return false;
							}

							public Object getState() {
								throw new RuntimeException("Method not implemented");
							}

							public SSLSession getSSLSession() {
								return null;
							}

							public HttpRoute getRoute() {
								return route;
							}

							public void bind(Socket socket) throws IOException {
							}

							public Socket getSocket() {
								return null;
							}
						};
					}

					public void abortRequest() {
					}
				};
			}

			public void releaseConnection(ManagedClientConnection conn, long validDuration, TimeUnit timeUnit) {
				try {
					conn.releaseConnection();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			public SchemeRegistry getSchemeRegistry() {
				return SchemeRegistryFactory.createDefault();
			}

			public void closeIdleConnections(long idletime, TimeUnit tunit) {
			}

			public void closeExpiredConnections() {
			}
		};
	}

	/**
	 * Set handler for requests. This allows to have custom behavior depending
	 * of requests, or even use assertions.
	 * 
	 * @param httpResponseExecutor
	 */
	public void setHttpResponseExecutor(HttpRequestExecutor httpResponseExecutor) {
		this.httpResponseExecutor = httpResponseExecutor;
	}

	/**
	 * Set next response. Will only work if no
	 * 
	 * @param response
	 */
	public void setResponse(HttpResponse response) {
		this.response = response;
	}

	public void setSleep(long sleep) {
		this.sleep = sleep;
	}

	public HttpRequest getSentRequest() {
		return sentRequest;
	}

	public int getOpenConnections() {
		return openConnections.get();
	}

}
