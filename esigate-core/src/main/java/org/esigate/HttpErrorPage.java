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
import java.io.PrintWriter;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.util.EntityUtils;
import org.esigate.http.BasicCloseableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exception thrown when an error occurred retrieving a resource.
 * <p>
 * This exception can include a HTTP response (with error page body) or a String body.
 * 
 * @author Francois-Xavier Bonnet
 * @author Nicolas Richeton
 */
public class HttpErrorPage extends Exception {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(HttpErrorPage.class);
    private final CloseableHttpResponse httpResponse;

    private static HttpEntity toMemoryEntity(String content) {
        return new StringEntity(content, "UTF-8");
    }

    private static HttpEntity toMemoryEntity(Exception exception) {
        StringBuilderWriter out = new StringBuilderWriter(Parameters.DEFAULT_BUFFER_SIZE);
        PrintWriter pw = new PrintWriter(out);
        exception.printStackTrace(pw);
        String content = out.toString();
        try {
            return toMemoryEntity(content);
        } finally {
            pw.close();
        }
    }

    private static HttpEntity toMemoryEntity(HttpEntity httpEntity) {
        if (httpEntity == null) {
            return null;
        }
        HttpEntity memoryEntity;
        try {
            byte[] content = EntityUtils.toByteArray(httpEntity);
            ByteArrayEntity byteArrayEntity = new ByteArrayEntity(content, ContentType.get(httpEntity));
            Header contentEncoding = httpEntity.getContentEncoding();
            if (contentEncoding != null) {
                byteArrayEntity.setContentEncoding(contentEncoding);
            }
            memoryEntity = byteArrayEntity;
        } catch (IOException e) {
            StringBuilderWriter out = new StringBuilderWriter(Parameters.DEFAULT_BUFFER_SIZE);
            PrintWriter pw = new PrintWriter(out);
            e.printStackTrace(pw);
            pw.close();
            memoryEntity = new StringEntity(out.toString(), ContentType.getOrDefault(httpEntity));
        }
        return memoryEntity;
    }

    /**
     * Create an HTTP error page exception from an Http response.
     * 
     * @param httpResponse
     *            backend response.
     */
    public HttpErrorPage(CloseableHttpResponse httpResponse) {
        super(httpResponse.getStatusLine().getStatusCode() + " " + httpResponse.getStatusLine().getReasonPhrase());
        this.httpResponse = httpResponse;
        // Consume the entity and replace it with an in memory Entity
        httpResponse.setEntity(toMemoryEntity(httpResponse.getEntity()));
    }

    /**
     * Create an HTTP response from a String content wich will be used as the response entity.
     * 
     * @param statusCode
     * @param statusMessage
     * @param content
     */
    public HttpErrorPage(int statusCode, String statusMessage, String content) {
        super(statusCode + " " + statusMessage);
        this.httpResponse = HttpErrorPage.generateHttpResponse(statusCode, statusMessage);
        this.httpResponse.setEntity(toMemoryEntity(content));
    }

    /**
     * Create an HTTP response from an exception. This exception stack trace will be showed to the end user.
     * 
     * @param statusCode
     * @param statusMessage
     * @param exception
     */
    public HttpErrorPage(int statusCode, String statusMessage, Exception exception) {
        super(statusCode + " " + statusMessage, exception);
        this.httpResponse = HttpErrorPage.generateHttpResponse(statusCode, statusMessage);
        this.httpResponse.setEntity(toMemoryEntity(exception));
    }

    /**
     * Get HTTP response enclosed in this exception.
     * 
     * @return HTTP response
     */
    public CloseableHttpResponse getHttpResponse() {
        return this.httpResponse;
    }

    public static CloseableHttpResponse generateHttpResponse(Exception exception) {
        if (exception instanceof HttpHostConnectException) {
            return generateHttpResponse(HttpStatus.SC_BAD_GATEWAY, "Connection refused");
        } else if (exception instanceof ConnectionPoolTimeoutException) {
            return generateHttpResponse(HttpStatus.SC_GATEWAY_TIMEOUT, "Connection pool timeout");
        } else if (exception instanceof ConnectTimeoutException) {
            return generateHttpResponse(HttpStatus.SC_GATEWAY_TIMEOUT, "Connect timeout");
        } else if (exception instanceof SocketTimeoutException) {
            return generateHttpResponse(HttpStatus.SC_GATEWAY_TIMEOUT, "Socket timeout");
        } else if (exception instanceof SocketException) {
            return generateHttpResponse(HttpStatus.SC_BAD_GATEWAY, "Socket Exception");
        } else if (exception instanceof ClientProtocolException) {
            String message = exception.getMessage();
            if (message == null && exception.getCause() != null) {
                message = exception.getCause().getMessage();
            }
            return generateHttpResponse(HttpStatus.SC_BAD_GATEWAY, "Protocol error: " + message);
        } else {
            LOG.error("Error retrieving URL", exception);
            return generateHttpResponse(HttpStatus.SC_BAD_GATEWAY, "Error retrieving URL");
        }
    }

    public static CloseableHttpResponse generateHttpResponse(int statusCode, String statusText) {
        CloseableHttpResponse result =
                BasicCloseableHttpResponse.adapt(new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1,
                        statusCode, statusText)));
        result.setEntity(toMemoryEntity(statusText));
        return result;
    }

}
