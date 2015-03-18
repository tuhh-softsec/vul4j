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

package org.esigate.servlet.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.esigate.Parameters;
import org.esigate.http.BasicCloseableHttpResponse;
import org.esigate.http.ContentTypeHelper;
import org.esigate.http.DateUtils;
import org.esigate.http.HttpResponseUtils;
import org.esigate.http.IncomingRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper to the HttpServletResponse that intercepts the content written in order to build an
 * {@link org.apache.http.HttpResponse}.
 * <ul>
 * <li>If the content of the response is required for transformation (parseable content-type or proxy=false) or smaller
 * than the buffer size the {@link org.apache.http.HttpResponse} will contain the entire response</li>
 * <li>If the content of the response is not required for transformation, the {@link org.apache.http.HttpResponse} will
 * contain only an abstract of the response truncated to the bufer size. The complete response will have already been
 * written to the original {@link HttpServletResponse}</li>
 * </ul>
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class ResponseCapturingWrapper extends HttpServletResponseWrapper {
    private static final Logger LOG = LoggerFactory.getLogger(ResponseCapturingWrapper.class);

    // OutputStream and Writer exposed
    private ServletOutputStream outputStream;
    private PrintWriter writer;

    // OutputStream and Writer of the wrapped HttpServletResponse
    private ServletOutputStream responseOutputStream;
    private PrintWriter responseWriter;

    // OutputStream and Writer buffers
    private ByteArrayOutputStream internalOutputStream;
    private StringBuilderWriter internalWriter;

    private HttpServletResponse response;

    private CloseableHttpResponse httpClientResponse = BasicCloseableHttpResponse.adapt(new BasicHttpResponse(
            new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK")));

    private String contentType;
    private String characterEncoding;
    private int bufferSize;
    private int bytesWritten = 0;
    private boolean committed = false;
    private ContentTypeHelper contentTypeHelper;
    private final boolean proxy;
    private boolean capture = true;
    private final ResponseSender responseSender;
    private final IncomingRequest incomingRequest;

    public ResponseCapturingWrapper(HttpServletResponse response, ContentTypeHelper contentTypeHelper, boolean proxy,
            int bufferSize, ResponseSender responseSender, IncomingRequest incomingRequest) {
        super(response);
        this.response = response;
        this.bufferSize = bufferSize;
        this.contentTypeHelper = contentTypeHelper;
        this.proxy = proxy;
        this.responseSender = responseSender;
        this.incomingRequest = incomingRequest;
    }

    @Override
    public void setStatus(int sc) {
        httpClientResponse.setStatusLine(new BasicStatusLine(HttpVersion.HTTP_1_1, sc, ""));
    }

    @Override
    public void setStatus(int sc, String sm) {
        httpClientResponse.setStatusLine(new BasicStatusLine(HttpVersion.HTTP_1_1, sc, sm));
    }

    public int getStatus() {
        return httpClientResponse.getStatusLine().getStatusCode();
    }

    @Override
    public void sendError(int sc, String msg) {
        httpClientResponse.setStatusLine(new BasicStatusLine(HttpVersion.HTTP_1_1, sc, msg));
    }

    @Override
    public void sendError(int sc) {
        httpClientResponse.setStatusLine(new BasicStatusLine(HttpVersion.HTTP_1_1, sc, ""));
    }

    @Override
    public void sendRedirect(String location) {
        httpClientResponse.setStatusLine(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_MOVED_TEMPORARILY,
                "Temporary redirect"));
        httpClientResponse.setHeader(HttpHeaders.LOCATION, location);
    }

    @Override
    public boolean containsHeader(String name) {
        return httpClientResponse.containsHeader(name);
    }

    @Override
    public void setHeader(String name, String value) {
        if (HttpHeaders.CONTENT_TYPE.equalsIgnoreCase(name)) {
            setContentType(value);
        } else {
            httpClientResponse.setHeader(name, value);
        }
    }

    @Override
    public void addHeader(String name, String value) {
        if (HttpHeaders.CONTENT_TYPE.equalsIgnoreCase(name)) {
            setContentType(value);
        } else {
            httpClientResponse.addHeader(name, value);
        }
    }

    @Override
    public void setDateHeader(String name, long date) {
        setHeader(name, DateUtils.formatDate(date));
    }

    @Override
    public void addDateHeader(String name, long date) {
        addHeader(name, DateUtils.formatDate(date));
    }

    @Override
    public void setIntHeader(String name, int value) {
        setHeader(name, Integer.toString(value));
    }

    @Override
    public void addIntHeader(String name, int value) {
        addHeader(name, Integer.toString(value));
    }

    @Override
    public void setContentLength(int len) {
        setHeader(HttpHeaders.CONTENT_LENGTH, Integer.toString(len));
    }

    @Override
    public void setCharacterEncoding(String charset) {
        this.characterEncoding = charset;
        updateContentTypeHeader();
    }

    @Override
    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    @Override
    public String getContentType() {
        Header contentTypeHeader = httpClientResponse.getFirstHeader(HttpHeaders.CONTENT_TYPE);
        if (contentTypeHeader != null) {
            return contentTypeHeader.getValue();
        } else {
            return null;
        }
    }

    @Override
    public void setContentType(String type) {
        ContentType parsedContentType = ContentType.parse(type);
        this.contentType = parsedContentType.getMimeType();
        if (parsedContentType.getCharset() != null) {
            this.characterEncoding = parsedContentType.getCharset().name();
        }
        updateContentTypeHeader();
    }

    private void updateContentTypeHeader() {
        if (contentType != null) {
            if (characterEncoding == null) {
                httpClientResponse.setHeader(HttpHeaders.CONTENT_TYPE, contentType);
            } else {
                httpClientResponse.setHeader(HttpHeaders.CONTENT_TYPE, contentType + ";charset=" + characterEncoding);
            }
        }
    }

    @Override
    public void setLocale(Locale loc) {
        response.setLocale(loc);
        if (characterEncoding == null) {
            characterEncoding = response.getCharacterEncoding();
            updateContentTypeHeader();
        }
    }

    @Override
    public Locale getLocale() {
        return response.getLocale();
    }

    @Override
    public void setBufferSize(int size) {
        this.bufferSize = size;
    }

    @Override
    public int getBufferSize() {
        return bufferSize;
    }

    @Override
    public void addCookie(Cookie cookie) {
        response.addCookie(cookie);
    }

    @Override
    public String encodeURL(String url) {
        return response.encodeURL(url);
    }

    @Override
    public String encodeRedirectURL(String url) {
        return response.encodeRedirectURL(url);
    }

    @SuppressWarnings("deprecation")
    @Override
    public String encodeUrl(String url) {
        return response.encodeUrl(url);
    }

    @SuppressWarnings("deprecation")
    @Override
    public String encodeRedirectUrl(String url) {
        return response.encodeRedirectUrl(url);
    }

    @Override
    public void reset() {
        if (isCommitted()) {
            throw new IllegalStateException("Response is already committed");
        }
        httpClientResponse =
                BasicCloseableHttpResponse.adapt(new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1,
                        HttpStatus.SC_OK, "OK")));
    }

    @Override
    public ServletOutputStream getOutputStream() {
        LOG.debug("getOutputStream");
        if (writer != null) {
            throw new IllegalStateException("Writer already obtained");
        }
        if (outputStream == null) {
            internalOutputStream = new ByteArrayOutputStream(Parameters.DEFAULT_BUFFER_SIZE);
            outputStream = new ServletOutputStream() {

                @Override
                public void write(int b) throws IOException {
                    if (capture || bytesWritten < bufferSize) {
                        internalOutputStream.write(b);
                    } else {
                        responseOutputStream.write(b);
                    }
                    bytesWritten++;
                    if (bytesWritten == bufferSize) {
                        commit();
                    }
                }

                @Override
                public void flush() throws IOException {
                    commit();
                }

                @Override
                public void close() throws IOException {
                    commit();
                }

                private void commit() throws IOException {
                    if (!committed) {
                        capture = hasToCaptureOutput();
                        if (!capture) {
                            responseSender.sendHeaders(httpClientResponse, incomingRequest, response);
                            responseOutputStream = response.getOutputStream();
                            internalOutputStream.writeTo(responseOutputStream);
                        }
                        committed = true;
                    }

                }

            };
        }
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() {
        LOG.debug("getWriter");
        if (outputStream != null) {
            throw new IllegalStateException("OutputStream already obtained");
        }
        if (writer == null) {
            internalWriter = new StringBuilderWriter(Parameters.DEFAULT_BUFFER_SIZE);
            writer = new PrintWriter(new Writer() {

                @Override
                public void write(char[] cbuf, int off, int len) throws IOException {
                    if (capture || bytesWritten < bufferSize) {
                        internalWriter.write(cbuf, off, len);
                    } else {
                        responseWriter.write(cbuf, off, len);
                    }
                    bytesWritten++;
                    if (bytesWritten == bufferSize) {
                        commit();
                    }
                }

                @Override
                public void flush() throws IOException {
                    commit();
                }

                @Override
                public void close() throws IOException {
                    commit();
                }

                private void commit() throws IOException {
                    if (!committed) {
                        capture = hasToCaptureOutput();
                        if (!capture) {
                            responseSender.sendHeaders(httpClientResponse, incomingRequest, response);
                            responseWriter = response.getWriter();
                            responseWriter.write(internalWriter.toString());
                        }
                        committed = true;
                    }

                }

            });
        }
        return writer;
    }

    @Override
    public void flushBuffer() throws IOException {
        if (outputStream != null) {
            outputStream.flush();
        }
        if (writer != null) {
            writer.flush();
        }
    }

    @Override
    public void resetBuffer() {
        if (isCommitted()) {
            throw new IllegalStateException("Response is already committed");
        }
        if (internalOutputStream != null) {
            internalOutputStream.reset();
        }
        if (internalWriter != null) {
            internalWriter = new StringBuilderWriter(Parameters.DEFAULT_BUFFER_SIZE);
        }
        bytesWritten = 0;
    }

    @Override
    public boolean isCommitted() {
        return committed;
    }

    /**
     * We have to capture the output of the response in 2 cases :
     * <ol>
     * <li>the content type is text and may have to be transformed</li>
     * <li>we are inside an include, in this case the content type must be considered as text</li>
     * </ol>
     * 
     * @return true if we have to capture the output of the response
     */
    private boolean hasToCaptureOutput() {
        return !proxy || contentTypeHelper.isTextContentType(httpClientResponse)
                || HttpResponseUtils.getFirstHeader(HttpHeaders.CONTENT_TYPE, httpClientResponse) == null;
    }

    /**
     * Returns the response. If the response has not been captured and has been written directly to the
     * {@link HttpServletResponse}, calling this method closes the HttpServletResponse writer OutputStream
     * 
     * @return the response
     */
    public CloseableHttpResponse getCloseableHttpResponse() {
        ContentType resultContentType = null;
        if (this.contentType != null) {
            resultContentType = ContentType.create(this.contentType, characterEncoding);
        }
        if (internalWriter != null) {
            writer.flush();
            httpClientResponse.setEntity(new StringEntity(internalWriter.toString(), resultContentType));
        } else if (internalOutputStream != null) {
            try {
                outputStream.flush();
            } catch (IOException e) {
                // Nothing to do;
            }
            httpClientResponse.setEntity(new ByteArrayEntity(internalOutputStream.toByteArray(), resultContentType));
        }
        if (!capture) {
            // The result has already been written to the response, let's close
            // the response stream
            if (responseWriter != null) {
                responseWriter.close();
            }
            if (responseOutputStream != null) {
                try {
                    responseOutputStream.close();
                } catch (IOException e) {
                    LOG.warn("Could not close servlet output stream: " + e.getMessage());
                }
            }
        }
        return httpClientResponse;
    }

}
