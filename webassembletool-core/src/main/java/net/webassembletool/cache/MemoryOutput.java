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
 * @see MemoryResource
 * 
 */
public class MemoryOutput extends Output {
	private final MemoryOutputStream out;

	/**
	 * Creates a MemoryOuput with the given size limit. If content exceeds
	 * maxSize, the whole content is NOT written and the resource will remain
	 * empty.
	 * 
	 * @param maxSize
	 *            Value &gt;=0. 0 means 'no limit'.
	 * 
	 */
	public MemoryOutput(int maxSize) {
		out = new MemoryOutputStream(maxSize);
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

	public MemoryResource toResource() {
		MemoryResource result;
		if (out.isTooBig()) {
			result = new MemoryResource();
		} else {
			result = new MemoryResource(out.toByteArray(), getCharsetName(),
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
	public static final class MemoryOutputStream extends ByteArrayOutputStream {
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
		public MemoryOutputStream(int maxSize) {
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
