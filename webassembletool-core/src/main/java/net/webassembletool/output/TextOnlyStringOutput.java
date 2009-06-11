package net.webassembletool.output;

import java.io.OutputStream;

import net.webassembletool.resource.ResourceUtils;

/**
 * TextOnlyStringOutput is a variant of string output which actually checks
 * whether content is of type text before buffering it. If no header indicates
 * whether this input is text the output is directly forwarded to binaryOutput
 * specified in construction time. For details on how text content is detected
 * look at {@link ResourceUtils#isTextContentType(String)}. The
 * {@link #hasTextBuffer()} method can be used to check whether the content
 * has been buffered. Notice that {@link #hasTextBuffer()} throws
 * IllegalStateException see its javadoc for details. Notice the nothing is done
 * in the fallback binary output until forwarding has been decided in open
 * method That is you can safley pass an output object that writes to http
 * resonse for example.
 * 
 * @author Omar BENHAMID
 */
public class TextOnlyStringOutput extends StringOutput {
    private final Output binaryOutput;
    private Boolean buffersText = null;

    /**
     * Creates TextOnlyStringOutput. If content at stream open time is
     * identified as being binary, it is simply forwarded to the binaryOutput
     * object.
     * 
     * @param binaryFallbackOutput binary output fallback stream
     */
    public TextOnlyStringOutput(Output binaryFallbackOutput) {
        super();
        this.binaryOutput = binaryFallbackOutput;
    }

    /**
     * Check whether this output has buffered text content or has forwarded it
     * to its fallback binary output considering it binary.
     * 
     * @return true if text content has been (or is beeing) buffered and false
     *         if it has been (is beeing) forwarded.
     * @throws IllegalStateException it this have not yet been decided. This
     *             happens when output is not yet opened and cann still receive
     *             more headers.
     */
    public boolean hasTextBuffer() throws IllegalStateException {
        if (buffersText == null)
            throw new IllegalStateException("Stream not yet open for output.");
        return buffersText.booleanValue();
    }

    /**
     * @see net.webassembletool.output.StringOutput#open()
     */
    @Override
    public void open() {
        if (ResourceUtils.isTextContentType(getHeader("Content-Type"))) {
            buffersText = Boolean.TRUE;
            super.open();
        } else {
            buffersText = Boolean.FALSE;
            binaryOutput.setStatus(getStatusCode(), getStatusMessage());
            copyHeaders(binaryOutput);
            binaryOutput.open();
        }
    }

    /**
     * @see net.webassembletool.output.StringOutput#close()
     */
    @Override
    public void close() {
        if (hasTextBuffer())
            super.close();
        else
            binaryOutput.close();
    }

    /**
     * @see net.webassembletool.output.StringOutput#getOutputStream()
     */
    @Override
    public OutputStream getOutputStream() {
        if (hasTextBuffer())
            return super.getOutputStream();
        else
            return binaryOutput.getOutputStream();
    }

}
