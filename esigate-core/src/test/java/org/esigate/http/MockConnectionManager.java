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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.HttpClientConnection;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.protocol.HttpContext;

public class MockConnectionManager implements HttpClientConnectionManager {
	private final AtomicBoolean open = new AtomicBoolean(false);
	private HttpResponse response;
	private HttpRequest sentRequest;
	private long sleep = 0l;

	private void sleep() {
		if (sleep > 0)
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
	}

	private final ConnectionRequest connectionRequest = new ConnectionRequest() {

		public boolean cancel() {
			return false;
		}

		public HttpClientConnection get(long timeout, TimeUnit tunit) throws InterruptedException, ExecutionException, ConnectionPoolTimeoutException {
			if (open.get())
				throw new IllegalStateException("Connection is busy");
			return httpClientConnection;
		}
	};
	private final HttpClientConnection httpClientConnection = new HttpClientConnection() {

		public void shutdown() throws IOException {
			open.set(false);
		}

		public void setSocketTimeout(int timeout) {
			// Nothing to do
		}

		public boolean isStale() {
			return !open.get();
		}

		public boolean isOpen() {
			return open.get();
		}

		public int getSocketTimeout() {
			return 0;
		}

		public HttpConnectionMetrics getMetrics() {
			return null;
		}

		public void close() throws IOException {
			open.set(false);
		}

		public void sendRequestHeader(HttpRequest request) throws HttpException, IOException {
			sentRequest = request;
		}

		public void sendRequestEntity(HttpEntityEnclosingRequest request) throws HttpException, IOException {
			sentRequest = request;
		}

		public HttpResponse receiveResponseHeader() throws HttpException, IOException {
			sleep();
			return execute(sentRequest);
		}

		public void receiveResponseEntity(HttpResponse response) throws HttpException, IOException {
			// Nothing to do
		}

		public boolean isResponseAvailable(int timeout) throws IOException {
			return true;
		}

		public void flush() throws IOException {
			// Nothing to do
		}
	};

	public ConnectionRequest requestConnection(HttpRoute route, Object state) {
		return connectionRequest;
	}

	public void releaseConnection(HttpClientConnection conn, Object newState, long validDuration, TimeUnit timeUnit) {
		open.set(false);
	}


	public void closeIdleConnections(long idletime, TimeUnit tunit) {
		// Nothing to do
	}

	public void closeExpiredConnections() {
		// Nothing to do
	}

	public void shutdown() {
		open.set(false);
	}

	public HttpRequest getSentRequest() {
		return sentRequest;
	}

	public void setResponse(HttpResponse response) {
		this.response = response;
	}

	public HttpResponse execute(@SuppressWarnings("unused") HttpRequest request) {
		// default implementation, can be overridden
		return response;
	}

	public final boolean isOpen() {
		return open.get();
	}

	public void connect(HttpClientConnection conn, HttpRoute route,
			int connectTimeout, HttpContext context) throws IOException {
		// Nothing to do
	}

	public void upgrade(HttpClientConnection conn, HttpRoute route,
			HttpContext context) throws IOException {
		// Nothing to do
	}

	public void routeComplete(HttpClientConnection conn, HttpRoute route,
			HttpContext context) throws IOException {
		// Nothing to do
	}

}
