package net.webassembletool.output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.servlet.http.HttpServletResponse;

import net.webassembletool.resource.ResourceUtils;

import org.apache.commons.io.IOUtils;

/**
 * TextOnlyStringOutput is a variant of string output which actually checks
 * whether content is of type text before buffering it. If no header indicates
 * whether this input is text the output is directly forwarded to binaryOutput
 * specified in construction time. For details on how text content is detected
 * look at {@link ResourceUtils#isTextContentType(String)}. The
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
	private final HttpServletResponse response;
	private ByteArrayOutputStream byteArrayOutputStream;
	private OutputStream outputStream;
	private boolean unzip = false;
	private boolean text = false;

	public TextOnlyStringOutput(HttpServletResponse response) {
		this.response = response;
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
		if (ResourceUtils.isTextContentType(getHeader("Content-Type")))
			text = true;
		copyHeaders();
		try {
			if (text) {
				String contentEncoding = getHeader("Content-encoding");
				if (contentEncoding != null) {
					unzip = true;
					if (!"gzip".equalsIgnoreCase(contentEncoding)
							&& !"x-gzip".equalsIgnoreCase(contentEncoding))
						throw new UnsupportedContentEncodingException(
								"Content-encoding \"" + contentEncoding
										+ "\" is not supported");
				}
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
	private void copyHeaders() {
		for (Iterator<Map.Entry<Object, Object>> headersIterator = getHeaders()
				.entrySet().iterator(); headersIterator.hasNext();) {
			Map.Entry<Object, Object> entry = headersIterator.next();
			// Swallow content-encoding and content-length headers for html
			// pages as content-length may change and gzip-encoded pages will be
			// decoded
			if (!text
					|| (!"content-length".equalsIgnoreCase((String) (entry
							.getKey())) && !"content-encoding"
							.equalsIgnoreCase((String) (entry.getKey()))))
				response.setHeader(entry.getKey().toString(), entry.getValue()
						.toString());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void close() {
		if (outputStream != null)
			try {
				outputStream.close();
			} catch (IOException e) {
				throw new OutputException(e);
			}
	}

	/**
	 * @see net.webassembletool.output.StringOutput#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() {
		if (byteArrayOutputStream != null)
			return byteArrayOutputStream;
		else
			return outputStream;
	}

	@Override
	public String toString() {
		if (byteArrayOutputStream == null)
			return "<Unparsed binary data: Content-Type="
					+ getHeader("Content-Type") + " >";
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
