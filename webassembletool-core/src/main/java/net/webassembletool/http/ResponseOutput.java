package net.webassembletool.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.webassembletool.output.Output;
import net.webassembletool.output.OutputException;

/**
 * Output implementation that simply writes to an HttpServletResponse.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class ResponseOutput extends Output {
	private final HttpServletResponse response;
	private OutputStream outputStream;

	public ResponseOutput(HttpServletResponse response) {
		this.response = response;
	}

	/** {@inheritDoc} */
	@Override
	public void open() {
		response.setStatus(getStatusCode());
		try {
			copyHeaders();
			outputStream = response.getOutputStream();
		} catch (IOException e) {
			throw new OutputException(e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public OutputStream getOutputStream() {
		return outputStream;
	}

	/**
	 * Copy all the headers to the response
	 */
	private void copyHeaders() {
		for (Iterator<Map.Entry<Object, Object>> headersIterator = getHeaders()
				.entrySet().iterator(); headersIterator.hasNext();) {
			Map.Entry<Object, Object> entry = headersIterator.next();
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
}
