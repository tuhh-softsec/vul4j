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

package org.esigate.test.conn;

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
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.HttpContext;

public class MockConnectionManager implements HttpClientConnectionManager {
    private final AtomicBoolean open = new AtomicBoolean(false);
    private IResponseHandler responseHandler;
    private HttpRequest sentRequest;
    private long sleep = 0L;

    private void sleep() {
        if (sleep > 0) {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private final ConnectionRequest connectionRequest = new ConnectionRequest() {

        @Override
        public boolean cancel() {
            return false;
        }

        @Override
        public HttpClientConnection get(long timeout, TimeUnit tunit) throws InterruptedException, ExecutionException,
                ConnectionPoolTimeoutException {
            if (open.get()) {
                throw new IllegalStateException("Connection is busy");
            }
            return httpClientConnection;
        }
    };
    private final HttpClientConnection httpClientConnection = new HttpClientConnection() {

        @Override
        public void shutdown() throws IOException {
            open.set(false);
        }

        @Override
        public void setSocketTimeout(int timeout) {
            // Nothing to do
        }

        @Override
        public boolean isStale() {
            return !open.get();
        }

        @Override
        public boolean isOpen() {
            return open.get();
        }

        @Override
        public int getSocketTimeout() {
            return 0;
        }

        @Override
        public HttpConnectionMetrics getMetrics() {
            return null;
        }

        @Override
        public void close() throws IOException {
            open.set(false);
        }

        @Override
        public void sendRequestHeader(HttpRequest request) throws HttpException, IOException {
            sentRequest = request;
        }

        @Override
        public void sendRequestEntity(HttpEntityEnclosingRequest request) throws HttpException, IOException {
            sentRequest = request;
        }

        @Override
        public HttpResponse receiveResponseHeader() throws HttpException, IOException {
            sleep();
            return execute(sentRequest);
        }

        @Override
        public void receiveResponseEntity(HttpResponse response) throws HttpException, IOException {
            // Nothing to do
        }

        @Override
        public boolean isResponseAvailable(int timeout) throws IOException {
            return true;
        }

        @Override
        public void flush() throws IOException {
            // Nothing to do
        }
    };

    @Override
    public ConnectionRequest requestConnection(HttpRoute route, Object state) {
        return connectionRequest;
    }

    @Override
    public void releaseConnection(HttpClientConnection conn, Object newState, long validDuration, TimeUnit timeUnit) {
        open.set(false);
    }

    @Override
    public void closeIdleConnections(long idletime, TimeUnit tunit) {
        // Nothing to do
    }

    @Override
    public void closeExpiredConnections() {
        // Nothing to do
    }

    @Override
    public void shutdown() {
        open.set(false);
    }

    public HttpRequest getSentRequest() {
        return sentRequest;
    }

    /**
     * Set the HttpResponse that will always be returned when calling the backend.
     * 
     * @param response
     */
    public void setResponse(final HttpResponse response) {
        setResponseHandler(new IResponseHandler() {
            @Override
            public HttpResponse execute(HttpRequest request) throws IOException {
                return response;
            }
        });
    }

    /**
     * Set a custom response handler. This allows to control and inject all responses from backend.
     * 
     * @param responseHandler
     */
    public void setResponseHandler(IResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
    }

    public HttpResponse execute(HttpRequest request) {
        try {
            return this.responseHandler.execute(request);
        } catch (IOException e) {
            return new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    e.getMessage());
        }
    }

    public final boolean isOpen() {
        return open.get();
    }

    @Override
    public void connect(HttpClientConnection conn, HttpRoute route, int connectTimeout, HttpContext context)
            throws IOException {
        // Nothing to do
    }

    @Override
    public void upgrade(HttpClientConnection conn, HttpRoute route, HttpContext context) throws IOException {
        // Nothing to do
    }

    @Override
    public void routeComplete(HttpClientConnection conn, HttpRoute route, HttpContext context) throws IOException {
        // Nothing to do
    }

}
