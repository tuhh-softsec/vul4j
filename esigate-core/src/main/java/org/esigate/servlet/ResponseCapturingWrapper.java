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

package org.esigate.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.output.WriterOutputStream;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.esigate.http.ContentTypeHelper;
import org.esigate.http.DateUtils;
import org.esigate.http.HttpResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseCapturingWrapper implements HttpServletResponse {
	private static final Logger LOG = LoggerFactory.getLogger(ResponseCapturingWrapper.class);
	private static final int DEFAULT_BUFFER_SIZE = 8192;
	private ServletOutputStream servletOutputStream;
	private PrintWriter jspWriter;
	private OutputStream outputStream;
	private StringWriter writer;
	private HttpServletResponse response;
	private HttpResponse httpClientResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));
	private String characterEncoding;
	private int bufferSize;
	private byte[] buffer;
	private int bytesWritten = 0;
	private boolean committed = false;
	private ContentTypeHelper contentTypeHelper;

	public ResponseCapturingWrapper(HttpServletResponse response, ContentTypeHelper contentTypeHelper) {
		this.response = response;
		this.characterEncoding = response.getCharacterEncoding();
		this.bufferSize = response.getBufferSize();
		if (this.bufferSize == 0)
			this.bufferSize = DEFAULT_BUFFER_SIZE;
		this.contentTypeHelper = contentTypeHelper;
	}

	@Override
	public boolean containsHeader(String name) {
		return httpClientResponse.containsHeader(name);
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {
		httpClientResponse.setStatusLine(new BasicStatusLine(HttpVersion.HTTP_1_1, sc, msg));
	}

	@Override
	public void sendError(int sc) throws IOException {
		httpClientResponse.setStatusLine(new BasicStatusLine(HttpVersion.HTTP_1_1, sc, ""));
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		httpClientResponse.setStatusLine(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_TEMPORARY_REDIRECT, "Temporary redirect"));
		httpClientResponse.setHeader(HttpHeaders.LOCATION, location);
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
	public void setStatus(int sc) {
		httpClientResponse.setStatusLine(new BasicStatusLine(HttpVersion.HTTP_1_1, sc, ""));
	}

	@Override
	public void setStatus(int sc, String sm) {
		httpClientResponse.setStatusLine(new BasicStatusLine(HttpVersion.HTTP_1_1, sc, sm));
	}

	@Override
	public void setCharacterEncoding(String charset) {
		this.characterEncoding = charset;
	}

	@Override
	public String getCharacterEncoding() {
		return this.characterEncoding;
	}

	@Override
	public String getContentType() {
		Header contentTypeHeader = httpClientResponse.getFirstHeader(HttpHeaders.CONTENT_TYPE);
		if (contentTypeHeader != null)
			return contentTypeHeader.getValue();
		else
			return null;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		LOG.debug("getOutputStream");
		if (jspWriter != null)
			throw new IllegalStateException("Writer already obtained");
		if (servletOutputStream == null) {
			initBuffer();
			servletOutputStream = new ServletOutputStream() {

				@Override
				public void write(int b) throws IOException {
					outputStream.write(b);
				}

				@Override
				public void close() throws IOException {
					outputStream.close();
				}
			};
		}
		return servletOutputStream;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		LOG.debug("getWriter");
		if (servletOutputStream != null)
			throw new IllegalStateException("OutputStream already obtained");
		if (jspWriter == null) {
			initBuffer();
			jspWriter = new PrintWriter(new Writer() {
				private final Charset charset = Charset.forName(characterEncoding);

				@Override
				public void write(char[] cbuf, int off, int len) throws IOException {
					outputStream.write(charset.encode(CharBuffer.wrap(cbuf, off, len)).array());
				}

				@Override
				public void flush() throws IOException {
					outputStream.flush();
				}

				@Override
				public void close() throws IOException {
					outputStream.close();
				}
			});
		}
		return jspWriter;
	}

	@Override
	public void setContentLength(int len) {
		setHeader(HttpHeaders.CONTENT_LENGTH, Integer.toString(len));
	}

	@Override
	public void setContentType(String type) {
		setHeader(HttpHeaders.CONTENT_TYPE, type);
		ContentType contentType = ContentType.parse(type);
		if (contentType.getCharset() != null)
			setCharacterEncoding(contentType.getCharset().name());
	}

	@Override
	public void setHeader(String name, String value) {
		httpClientResponse.setHeader(name, value);
	}

	@Override
	public void addHeader(String name, String value) {
		httpClientResponse.addHeader(name, value);
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
	public void setBufferSize(int size) {
		this.bufferSize = size;
	}

	@Override
	public int getBufferSize() {
		return bufferSize;
	}

	@Override
	public void flushBuffer() throws IOException {
		if (outputStream != null)
			outputStream.flush();
	}

	@Override
	public void resetBuffer() {
		if (isCommitted())
			throw new IllegalStateException("Response is already committed");
		bytesWritten = 0;
	}

	@Override
	public boolean isCommitted() {
		return committed;
	}

	@Override
	public void reset() {
		if (isCommitted())
			throw new IllegalStateException("Response is already committed");
		httpClientResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "Ok"));
	}

	@Override
	public void setLocale(Locale loc) {
		response.setLocale(loc);
		if (characterEncoding == null)
			characterEncoding = response.getCharacterEncoding();
	}

	@Override
	public Locale getLocale() {
		return response.getLocale();
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

	private void initBuffer() {
		buffer = new byte[bufferSize];
		bytesWritten = 0;
		outputStream = new OutputStream() {

			@Override
			public void write(int b) throws IOException {
				buffer[bytesWritten] = (byte) b;
				bytesWritten++;
				if (bytesWritten == bufferSize) {
					flushInternalBuffer();
				}
			}

			@Override
			public void close() throws IOException {
				flushInternalBuffer();
				outputStream.close();
			}

			@Override
			public void flush() throws IOException {
				flushInternalBuffer();
			}
		};
	}

	private boolean hasToCaptureOutput() {
		return contentTypeHelper.isTextContentType(httpClientResponse) || HttpResponseUtils.getFirstHeader(HttpHeaders.CONTENT_TYPE, httpClientResponse) == null;
	}

	private void flushInternalBuffer() throws IOException {
		LOG.debug("flushInternalBuffer");
		if (hasToCaptureOutput()) {
			String charsetName = characterEncoding;
			if (charsetName == null)
				charsetName = "ISO-8859-1";
			String contentType = HttpResponseUtils.getFirstHeader(HttpHeaders.CONTENT_TYPE, httpClientResponse);
			if (contentType == null)
				httpClientResponse.addHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=" + charsetName);
			writer = new StringWriter();
			outputStream = new WriterOutputStream(writer, charsetName);
		} else {
			response.setStatus(httpClientResponse.getStatusLine().getStatusCode());
			for (Header header : httpClientResponse.getAllHeaders()) {
				String name = header.getName();
				String value = header.getValue();
				response.addHeader(name, value);
			}
			outputStream = response.getOutputStream();
		}
		outputStream.write(buffer, 0, bytesWritten);
		outputStream.flush();
		committed = true;
	}

	public HttpResponse getResponse() {
		if (hasToCaptureOutput()) {
			try {
				flushBuffer();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String result = "";
			if (writer != null)
				result = writer.toString();
			if (result.isEmpty() && httpClientResponse.getStatusLine().getStatusCode() >= 400) {
				result = httpClientResponse.getStatusLine().getStatusCode() + " " + httpClientResponse.getStatusLine().getReasonPhrase();
				setContentType("text/html;charset=ISO-8859-1");
			}
			httpClientResponse.setEntity(new StringEntity(result, ContentType.parse(getContentType())));
			writer = null;
		}
		return httpClientResponse;
	}
}
