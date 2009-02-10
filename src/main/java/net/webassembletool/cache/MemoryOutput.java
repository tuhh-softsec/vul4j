package net.webassembletool.cache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.webassembletool.output.Output;
import net.webassembletool.output.OutputException;

/**
 * Output implementation that stores the file and headers to a MemoryResource.
 * 
 * @author François-Xavier Bonnet
 * @see MemoryResource
 * 
 */
public class MemoryOutput extends Output {
    private final MemoryOutputStream out;

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

    public static final class MemoryOutputStream extends ByteArrayOutputStream {
        private final int maxSize;
        private boolean tooBig = false;

        public MemoryOutputStream(int maxSize) {
            this.maxSize = maxSize;
        }

        /** {@inheritDoc} */
        @Override
        public void write(int b) {
            byte buf[] = new byte[] { (byte) b };
            write(buf, 0, 1);
        }

        /** {@inheritDoc} */
        @Override
        public void write(byte[] b, int off, int len) {
            if (!tooBig) {
                if ((count + len) > maxSize) {
                    tooBig = true;
                } else {
                    super.write(b, off, len);
                }
            }
        }

        public boolean isTooBig() {
            return tooBig;
        }

        public void clear() {
            count = 0;
            buf = new byte[32];
        }
    }
}
