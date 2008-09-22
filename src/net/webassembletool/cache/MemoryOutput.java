package net.webassembletool.cache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import net.webassembletool.ouput.Output;

/**
 * Output implementation that stores the file and headers to a MemoryResource.
 * 
 * @author François-Xavier Bonnet
 * @see MemoryResource
 * 
 */
public class MemoryOutput extends Output {
    private byte[] byteArray;
    private ByteArrayOutputStream byteArrayOutputStream;
    private int maxSize = 0;
    private int size;
    private boolean tooBig = false;

    public MemoryOutput(int maxSize) {
	this.maxSize = maxSize;
    }

    @Override
    public void open() {
	byteArrayOutputStream = new ByteArrayOutputStream();
	size = 0;
    }

    /**
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(int i) throws IOException {
	if (!tooBig) {
	    size++;
	    if (size > maxSize)
		tooBig = true;
	    else
		byteArrayOutputStream.write(i);
	}
    }

    @Override
    public void close() {
	byteArray = byteArrayOutputStream.toByteArray();
	try {
	    byteArrayOutputStream.close();
	} catch (IOException e) {
	    // Should not happen
	}
	byteArrayOutputStream = null;
    }

    public MemoryResource toResource() {
	if (tooBig)
	    return new MemoryResource();
	return new MemoryResource(byteArray, getCharsetName(), getHeaders(),
		getStatusCode(), getStatusMessage());
    }
}
