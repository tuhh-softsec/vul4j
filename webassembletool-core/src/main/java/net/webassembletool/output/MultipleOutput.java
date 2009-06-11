package net.webassembletool.output;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Output implementation that forwards data received to several Outputs at the
 * same time.
 * 
 * @author François-Xavier Bonnet
 * 
 */
public class MultipleOutput extends Output {
    private final ArrayList<Output> outputs = new ArrayList<Output>();
    private OutputStream output;

    public MultipleOutput() {
        super();
    }

    /**
     * Adds an Output to the list of Outputs to which the MultipleOuput should
     * forward data.
     * 
     * @param output An Output to add to the list
     */
    public void addOutput(Output output) {
        outputs.add(output);
    }

    @Override
    public void open() {
        for (Output output : outputs) {
            copyHeaders(output);
            output.setStatus(getStatusCode(), getStatusMessage());
            output.setCharsetName(getCharsetName());
            output.open();
        }
        output = ChainedOutputStream.createChain(outputs);
    }

    /** {@inheritDoc} */
    @Override
    public OutputStream getOutputStream() {
        return output;
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        try {
            if (output != null) {
                output.close();
            }
            for (Output output : outputs) {
                output.close();
            }
        } catch (IOException e) {
            throw new OutputException(e);
        }
    }

    // private final static class MultipleOutputStream extends OutputStream {
    // private final List<OutputStream> dest;
    //
    // public MultipleOutputStream(List<Output> outputs) {
    // dest = new ArrayList<OutputStream>(outputs.size());
    // for (Output output : outputs) {
    // dest.add(output.getOutputStream());
    // }
    // }
    //
    // /** {@inheritDoc} */
    // @Override
    // public void write(int b) throws IOException {
    // byte buf[] = new byte[] { (byte) b };
    // write(buf, 0, 1);
    // }
    //
    // /** {@inheritDoc} */
    // @Override
    // public void write(byte[] b, int off, int len) throws IOException {
    // for (OutputStream out : dest) {
    // out.write(b, off, len);
    // }
    // }
    //
    // /** {@inheritDoc} */
    // @Override
    // public void close() throws IOException {
    // for (OutputStream out : dest) {
    // out.close();
    // }
    // }
    // }

    public final static class ChainedOutputStream extends FilterOutputStream {
        private final OutputStream next;

        public ChainedOutputStream(OutputStream dest, OutputStream next) {
            super(dest);
            this.next = next;
        }

        /** {@inheritDoc} */
        @Override
        public void write(int b) throws IOException {
            byte buf[] = new byte[] { (byte) b };
            write(buf, 0, 1);
        }

        /** {@inheritDoc} */
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            // super.write(b, off, len); BUG: causes StackOverflowError
            out.write(b, off, len);
            next.write(b, off, len);
        }

        /** {@inheritDoc} */
        @Override
        public void close() throws IOException {
            super.close();
            next.close();
        }

        /** {@inheritDoc} */
        @Override
        public void flush() throws IOException {
            super.flush();
            next.flush();
        }

        public static OutputStream createChain(List<Output> outputs) {
            OutputStream current = null;
            OutputStream previous = null;
            for (Output output : outputs) {
                if (previous == null) {
                    current = output.getOutputStream();
                } else {
                    current = new ChainedOutputStream(output.getOutputStream(),
                            previous);
                }
                previous = current;
            }
            return current;
        }
    }
}
