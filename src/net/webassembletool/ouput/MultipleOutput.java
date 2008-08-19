package net.webassembletool.ouput;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Output implementation that forwards data received to several Outputs at the
 * same time.
 * 
 * @author François-Xavier Bonnet
 * 
 */
public class MultipleOutput implements Output {
    private ArrayList<Output> outputs = new ArrayList<Output>();

    /**
     * Adds an Output to the list of Outputs to which the MultipleOuput should
     * forward data.
     * 
     * @param output
     *            An Output to add to the list
     */
    public void addOutput(Output output) {
	outputs.add(output);
    }

    public void addHeader(String name, String value) {
	for (Iterator<Output> iterator = outputs.iterator(); iterator.hasNext();) {
	    Output output = iterator.next();
	    output.addHeader(name, value);
	}
    }

    public void close() {
	for (Iterator<Output> iterator = outputs.iterator(); iterator.hasNext();) {
	    Output output = iterator.next();
	    output.close();
	}
    }

    public void open() {
	for (Iterator<Output> iterator = outputs.iterator(); iterator.hasNext();) {
	    Output output = iterator.next();
	    output.open();
	}
    }

    public void setCharset(String charset) {
	for (Iterator<Output> iterator = outputs.iterator(); iterator.hasNext();) {
	    Output output = iterator.next();
	    output.setCharset(charset);
	}
    }

    public void write(byte[] bytes, int offset, int length) {
	for (Iterator<Output> iterator = outputs.iterator(); iterator.hasNext();) {
	    Output output = iterator.next();
	    output.write(bytes, offset, length);
	}
    }

    public void setStatus(int code, String message) {
	for (Iterator<Output> iterator = outputs.iterator(); iterator.hasNext();) {
	    Output output = iterator.next();
	    output.setStatus(code, message);
	}

    }
}
