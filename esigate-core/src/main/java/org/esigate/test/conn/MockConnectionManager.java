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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpClientConnection;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.protocol.HttpContext;
import org.esigate.HttpErrorPage;

public class MockConnectionManager implements HttpClientConnectionManager {
    private final AtomicInteger openConnections = new AtomicInteger(0);
    private IResponseHandler responseHandler;
    private HttpRequest lastSentRequest;
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

    @Override
    public ConnectionRequest requestConnection(HttpRoute route, Object state) {
        return new ConnectionRequest() {

            @Override
            public boolean cancel() {
                return false;
            }

            @Override
            public HttpClientConnection get(long timeout, TimeUnit tunit) {
                openConnections.incrementAndGet();
                return new HttpClientConnection() {
                    private boolean open = true;
                    private HttpRequest request;

                    @Override
                    public void shutdown() {
                        close();
                    }

                    @Override
                    public void setSocketTimeout(int timeout) {
                        // Nothing to do
                    }

                    @Override
                    public boolean isStale() {
                        return !open;
                    }

                    @Override
                    public boolean isOpen() {
                        return open;
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
                    public void close() {
                        openConnections.decrementAndGet();
                        open = false;
                    }

                    @Override
                    public void sendRequestHeader(HttpRequest request) {
                        lastSentRequest = request;
                        this.request = request;
                    }

                    @Override
                    public void sendRequestEntity(HttpEntityEnclosingRequest request) {
                        lastSentRequest = request;
                        this.request = request;
                    }

                    @Override
                    public HttpResponse receiveResponseHeader() {
                        sleep();
                        return execute(this.request);
                    }

                    @Override
                    public void receiveResponseEntity(HttpResponse response) {
                        // Nothing to do
                    }

                    @Override
                    public boolean isResponseAvailable(int timeout) {
                        return true;
                    }

                    @Override
                    public void flush() {
                        // Nothing to do
                    }
                };
            }
        };
    }

    @Override
    public void releaseConnection(HttpClientConnection conn, Object newState, long validDuration, TimeUnit timeUnit) {
        try {
            conn.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        if (openConnections.get() > 0)
            throw new RuntimeException("Some connections were not released!");
    }

    public HttpRequest getSentRequest() {
        return lastSentRequest;
    }

    /**
     * Set the HttpResponse that will always be returned when calling the backend.
     * 
     * @param response
     */
    public void setResponse(final HttpResponse response) {
        setResponseHandler(new IResponseHandler() {
            @Override
            public HttpResponse execute(HttpRequest request) {
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
            return HttpErrorPage.generateHttpResponse(e);
        }
    }

    public final boolean hasOpenConnections() {
        return openConnections.get() > 0;
    }

    @Override
    public void connect(HttpClientConnection conn, HttpRoute route, int connectTimeout, HttpContext context) {
        if (this.responseHandler instanceof IResponseHandler2) {
            ((IResponseHandler2) this.responseHandler).connect(conn, route, connectTimeout, context);
        }
    }

    @Override
    public void upgrade(HttpClientConnection conn, HttpRoute route, HttpContext context) {
        // Nothing to do
    }

    @Override
    public void routeComplete(HttpClientConnection conn, HttpRoute route, HttpContext context) {
        // Nothing to do
    }

}
