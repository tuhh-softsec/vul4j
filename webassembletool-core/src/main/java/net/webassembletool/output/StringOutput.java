package net.webassembletool.output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

/**
 * Output implementation that writes to a String.<br />
 * StringOutput should be used only for text responses such as HTML and not for
 * binary data such as images.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class StringOutput extends Output {
	private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	private boolean unzip = false;

	public StringOutput() {
		super();
	}

	public String getLocation() {
		return getHeader("location");
	}

	/** {@inheritDoc} */
	@Override
	public void open() {
		String contentEncoding = getHeader("Content-encoding");
		if (contentEncoding != null) {
			unzip = true;
			if (!"gzip".equalsIgnoreCase(contentEncoding)
					&& !"x-gzip".equalsIgnoreCase(contentEncoding))
				throw new UnsupportedContentEncodingException(
						"Content-encoding \"" + contentEncoding
								+ "\" is not supported");
		}
	}

	/** {@inheritDoc} */
	@Override
	public OutputStream getOutputStream() {
		return byteArrayOutputStream;
	}

	/** {@inheritDoc} */
	@Override
	public void close() {
		try {
			byteArrayOutputStream.close();
		} catch (IOException e) {
			// should not happen
			throw new OutputException(e);
		}
	}

	public void copyHeaders(HttpServletResponse response) {
		for (Entry<?, ?> entry : getHeaders().entrySet()) {
			String key = entry.getKey().toString();
			String value = entry.getValue().toString();
			// Swallow content-encoding and content-length headers as they will
			// change
			if (!"content-length".equalsIgnoreCase(key)
					&& !"content-encoding".equalsIgnoreCase(key)) {
				response.addHeader(key, value);
			}
		}
	}

	@Override
	public String toString() {
		String charsetName = getCharsetName();
		if (charsetName == null)
			charsetName = "ISO-8859-1";
		try {
			if (unzip) {
				// Unzip the stream if necessary
				GZIPInputStream gzipInputStream = new GZIPInputStream(
						new ByteArrayInputStream(byteArrayOutputStream
								.toByteArray()));
				ByteArrayOutputStream unzippedResult = new ByteArrayOutputStream();
				IOUtils.copy(gzipInputStream, unzippedResult);
				return unzippedResult.toString(charsetName);
			} else {
				return byteArrayOutputStream.toString(charsetName);
			}
		} catch (UnsupportedEncodingException e) {
			throw new OutputException(e);
		} catch (IOException e) {
			throw new OutputException(e);
		}
	}

}
