package net.webassembletool.ouput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Output implementation that forwards data received to several Outputs at the
 * same time.
 * 
 * @author François-Xavier Bonnet
 * 
 */
public class MultipleOutput extends Output {
    private final ArrayList<Output> outputs = new ArrayList<Output>();

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
	for (Iterator<Output> iterator = outputs.iterator(); iterator.hasNext();) {
	    Output output = iterator.next();
	    for (Iterator<Map.Entry<Object, Object>> headersIterator = getHeaders()
		    .entrySet().iterator(); headersIterator.hasNext();) {
		Map.Entry<Object, Object> entry = headersIterator.next();
		output.addHeader(entry.getKey().toString(), entry.getValue()
			.toString());
	    }
	    output.setStatus(getStatusCode(), getStatusMessage());
	    output.setCharsetName(getCharsetName());
	    output.open();
	}
    }

    /**
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(int i) throws IOException {
	for (Iterator<Output> iterator = outputs.iterator(); iterator.hasNext();) {
	    Output output = iterator.next();
	    output.write(i);
	}

    }

    @Override
    public void close() {
	for (Iterator<Output> iterator = outputs.iterator(); iterator.hasNext();) {
	    Output output = iterator.next();
	    try {
		output.close();
	    } catch (IOException e) {
		throw new OutputException(e);
	    }
	}
    }
}
