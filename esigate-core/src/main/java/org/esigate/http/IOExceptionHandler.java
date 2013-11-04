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
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

public final class IOExceptionHandler {

    private IOExceptionHandler() {

    }

    public static HttpResponse toHttpResponse(IOException exception) {
        if (exception instanceof HttpHostConnectException) {
            int statusCode = HttpStatus.SC_BAD_GATEWAY;
            String statusText = "Connection refused";
            return new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, statusCode, statusText));
        } else if (exception instanceof ConnectionPoolTimeoutException) {
            int statusCode = HttpStatus.SC_GATEWAY_TIMEOUT;
            String statusText = "Connection pool timeout";
            return new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, statusCode, statusText));
        } else if (exception instanceof ConnectTimeoutException) {
            int statusCode = HttpStatus.SC_GATEWAY_TIMEOUT;
            String statusText = "Connect timeout";
            return new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, statusCode, statusText));
        } else if (exception instanceof SocketTimeoutException) {
            int statusCode = HttpStatus.SC_GATEWAY_TIMEOUT;
            String statusText = "Socket timeout";
            return new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, statusCode, statusText));
        } else if (exception instanceof SocketException) {
            int statusCode = HttpStatus.SC_BAD_GATEWAY;
            String statusText = "Socket Exception";
            return new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, statusCode, statusText));
        } else {
            int statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
            String statusText = "Error retrieving URL";
            return new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, statusCode, statusText));
        }
    }

}
