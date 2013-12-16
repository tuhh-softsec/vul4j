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
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.util.EntityUtils;

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
    private final HttpResponse httpResponse;

    /**
     * Create an HTTP error page exception from an Http response.
     * 
     * @param httpResponse
     *            backend response.
     */
    public HttpErrorPage(HttpResponse httpResponse) {
        super(httpResponse.getStatusLine().getStatusCode() + " " + httpResponse.getStatusLine().getReasonPhrase());
        this.httpResponse = httpResponse;
        // Consume the entity and replace it with an in memory Entity
        HttpEntity httpEntity = httpResponse.getEntity();
        HttpEntity memoryEntity;
        if (httpEntity != null) {
            try {
                byte[] content = EntityUtils.toByteArray(httpEntity);
                ByteArrayEntity byteArrayEntity = new ByteArrayEntity(content, ContentType.get(httpEntity));
                Header contentEncoding = httpEntity.getContentEncoding();
                if (contentEncoding != null) {
                    byteArrayEntity.setContentEncoding(contentEncoding);
                }
                memoryEntity = byteArrayEntity;
            } catch (IOException e) {
                StringBuilderWriter out = new StringBuilderWriter(512);
                PrintWriter pw = new PrintWriter(out);
                e.printStackTrace(pw);
                pw.close();
                memoryEntity = new StringEntity(out.toString(), ContentType.getOrDefault(httpEntity));
            }
            this.httpResponse.setEntity(memoryEntity);
        }
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
        this.httpResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, statusCode, statusMessage));
        try {
            this.httpResponse.setEntity(new StringEntity(content, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // This should not happen as UTF-8 is always supported
            throw new RuntimeException(e);
        }
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
        this.httpResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, statusCode, statusMessage));
        StringBuilderWriter out = new StringBuilderWriter(512);
        PrintWriter pw = new PrintWriter(out);
        exception.printStackTrace(pw);
        String content = out.toString();
        try {
            this.httpResponse.setEntity(new StringEntity(content, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // This should not happen as UTF-8 is always supported
            throw new RuntimeException(e);
        } finally {
            pw.close();
        }
    }

    /**
     * Get HTTP response enclosed in this exception.
     * 
     * @return HTTP response
     */
    public HttpResponse getHttpResponse() {
        return this.httpResponse;
    }

}
