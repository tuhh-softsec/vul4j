package net.webassembletool.cache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.webassembletool.output.Output;
import net.webassembletool.output.OutputException;

/**
 * Output implementation that stores the file and headers to a MemoryResource.
 * 
 * @author Francois-Xavier Bonnet
 * @author nricheton
 * @see CachedResponse
 * 
 */
public class CacheOutput extends Output {
	private final MaxSizedOutputStream out;

	/**
	 * Creates a MemoryOuput with the given size limit. If content exceeds
	 * maxSize, the whole content is NOT written and the resource will remain
	 * empty.
	 * 
	 * @param maxSize
	 *            Value &gt;=0. 0 means 'no limit'.
	 * 
	 */
	public CacheOutput(int maxSize) {
		out = new MaxSizedOutputStream(maxSize);
	}

	/** {@inheritDoc} */
	@Override
	public void open() {
		// nothing to do
	}

	/** {@inheritDoc} */
	@Override
	public OutputStream getOutputStream() {
		return out;
	}

	/** {@inheritDoc} */
	@Override
	public void close() {
		try {
			out.close();
		} catch (IOException e) {
			// should not happen
			throw new OutputException(e);
		}
	}

	public CachedResponse toResource() {
		CachedResponse result;
		if (out.isTooBig()) {
			result = null;
		} else {
			result = new CachedResponse(out.toByteArray(), getCharsetName(),
					getHeaders(), getStatusCode(), getStatusMessage());
		}
		out.clear();
		return result;
	}

	/**
	 * Local implementation of an output stream for MemoryOuput.
	 * 
	 * @author Francois-Xavier Bonnet
	 * @author nricheton
	 * 
	 */
	public static final class MaxSizedOutputStream extends ByteArrayOutputStream {
		private final int maxSize;
		private boolean tooBig = false;

		/**
		 * 
		 * Creates a MemoryOutputStream with the given size limit. If content
		 * exceeds maxSize, the whole content is NOT written and the resource
		 * will remain empty.
		 * 
		 * @param maxSize
		 *            Value &gt;=0. 0 means 'no limit'.
		 */
		public MaxSizedOutputStream(int maxSize) {
			this.maxSize = maxSize;
		}

		/** {@inheritDoc} */
		@Override
		public synchronized void write(int b) {
			byte buf[] = new byte[] { (byte) b };
			write(buf, 0, 1);
		}

		/** {@inheritDoc} */
		@Override
		public synchronized void write(byte[] b, int off, int len) {
			if (!tooBig) {
				if (maxSize != 0 && (count + len) > maxSize) {
					tooBig = true;
				} else {
					super.write(b, off, len);
				}
			}
		}

		/**
		 * Returns true if the content written did not reach maxSize.
		 * 
		 * @return true if content size exceeds maxSize
		 */
		public boolean isTooBig() {
			return tooBig;
		}

		public void clear() {
			count = 0;
			buf = new byte[32];
		}
	}
}
