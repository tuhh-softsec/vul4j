package org.esigate.output;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.esigate.api.HttpResponse;
import org.esigate.resource.ResourceUtils;


/**
 * TextOnlyStringOutput is a variant of string output which actually checks
 * whether content is of type text before buffering it. If no header indicates
 * whether this input is text the output is directly forwarded to binaryOutput
 * specified in construction time. For details on how text content is detected
 * look at {@link ResourceUtils#isTextContentType(String, List)}. The
 * {@link #hasTextBuffer()} method can be used to check whether the content has
 * been buffered. Notice that {@link #hasTextBuffer()} throws
 * IllegalStateException see its javadoc for details. Notice the nothing is done
 * in the fallback binary output until forwarding has been decided in open
 * method That is you can safley pass an output object that writes to http
 * resonse for example.
 * 
 * @author Omar BENHAMID
 * @author Francois-Xavier Bonnet
 */
public class TextOnlyStringOutput extends Output {
	private final HttpResponse response;
	private final Collection<String> contentTypes;
	private ByteArrayOutputStream byteArrayOutputStream;
	private OutputStream outputStream;

	public TextOnlyStringOutput(HttpResponse response, Collection<String> contentTypes) {
		this.response = response;
		this.contentTypes = contentTypes;
	}

	/**
	 * Check whether this output has buffered text content or has forwarded it
	 * to its fallback binary output considering it binary.
	 * 
	 * @return true if text content has been (or is beeing) buffered and false
	 *         if it has been (is beeing) forwarded.
	 * @throws IllegalStateException
	 *             it this have not yet been decided. This happens when output
	 *             is not yet opened and cann still receive more headers.
	 */
	public boolean hasTextBuffer() throws IllegalStateException {
		return byteArrayOutputStream != null;
	}

	/** {@inheritDoc} */
	@Override
	public void open() {
		response.setStatus(getStatusCode());
		boolean text = ResourceUtils.isTextContentType(getHeader("Content-Type"), this.contentTypes);
		copyHeaders(text);
		try {
			if (text) {
				byteArrayOutputStream = new ByteArrayOutputStream();
			} else {
				outputStream = response.getOutputStream();
			}
		} catch (IOException e) {
			throw new OutputException(e);
		}
	}

	/**
	 * Copy all the headers to the response
	 */
	private void copyHeaders(boolean text) {
		for (Entry<String, Set<String>> entry : getHeaders().entrySet()) {
			// Swallow content-encoding and content-length headers for html
			// pages as content-length may change and gzip-encoded pages will be
			// decoded
			if (!text || (!"content-length".equalsIgnoreCase(entry.getKey()))) {
				Set<String> values = entry.getValue();
				for (String value : values) {
					response.addHeader(entry.getKey(), value);
				}
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public void close() {
		if (outputStream != null) {
			try {
				outputStream.close();
			} catch (IOException e) {
				throw new OutputException(e);
			}
		}
	}

	/**
	 * @see org.esigate.output.StringOutput#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() {
		if (byteArrayOutputStream != null) {
			return byteArrayOutputStream;
		} else {
			return outputStream;
		}
	}

	@Override
	public String toString() {
		if (byteArrayOutputStream == null) {
			return "<Unparsed binary data: Content-Type="
					+ getHeader("Content-Type") + " >";
		}
		String charsetName = getCharsetName();
		if (charsetName == null) {
			charsetName = "ISO-8859-1";
		}
		try {
			return byteArrayOutputStream.toString(charsetName);
		} catch (UnsupportedEncodingException e) {
			throw new OutputException(e);
		}
	}
}
