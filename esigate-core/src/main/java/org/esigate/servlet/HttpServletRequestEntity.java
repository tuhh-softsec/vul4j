package org.esigate.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;

public class HttpServletRequestEntity implements HttpEntity {
	private final HttpServletRequest request;
	private final long contentLength;
	private final Header contentType;
	private final Header contentEncoding;
	private InputStream inputStream;

	HttpServletRequestEntity(HttpServletRequest request) {
		this.request = request;
		String contentLengthHeader = request.getHeader(HttpHeaders.CONTENT_LENGTH);
		contentLength = (contentLengthHeader != null) ? Long.parseLong(contentLengthHeader) : -1;
		String contentTypeHeader = request.getContentType();
		if (request.getContentType() != null) {
			contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, contentTypeHeader);
		} else
			contentType = null;
		String contentEncodingHeader = request.getCharacterEncoding();
		if (contentEncodingHeader != null) {
			contentEncoding = new BasicHeader(HttpHeaders.CONTENT_ENCODING, contentEncodingHeader);
		} else
			contentEncoding = null;
	}

	public boolean isRepeatable() {
		return false;
	}

	public boolean isChunked() {
		return false;
	}

	public long getContentLength() {
		return contentLength;
	}

	public Header getContentType() {
		return contentType;
	}

	public Header getContentEncoding() {
		return contentEncoding;
	}

	public InputStream getContent() throws IOException, IllegalStateException {
		if (inputStream == null)
			inputStream = request.getInputStream();
		return inputStream;
	}

	public void writeTo(OutputStream outstream) throws IOException {
		IOUtils.copy(getContent(), outstream);
	}

	public boolean isStreaming() {
		return true;
	}

	public void consumeContent() throws IOException {
		IOUtils.closeQuietly(getContent());
	}

}
