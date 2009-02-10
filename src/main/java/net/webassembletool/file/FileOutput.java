package net.webassembletool.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.webassembletool.output.Output;
import net.webassembletool.output.OutputException;

/**
 * Output implementation that saves the file and headers into two distinct
 * files.
 * 
 * @author François-Xavier Bonnet
 * 
 */
public class FileOutput extends Output {
    private File file;
    private final File headerFile;
    private FileOutputStream fileOutputStream;

    public FileOutput(String url) {
	file = new File(url);
	headerFile = new File(url + ".headers");
    }

    /** {@inheritDoc} */
    @Override
    public void open() {
	try {
	    if (!file.exists()) {
		file.getParentFile().mkdirs();
		file.createNewFile();
	    }
	    fileOutputStream = new FileOutputStream(file);
	} catch (IOException e) {
	    throw new OutputException("Could not create file: " + file.toURI(),
		    e);
	}
    }

    /** {@inheritDoc} */
    @Override
    public OutputStream getOutputStream() {
	return fileOutputStream;
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
	try {
	    // In case the file could not be written, fileOutputStream might be
	    // null
	    if (fileOutputStream != null)
		fileOutputStream.close();
	    fileOutputStream = null;
	    file = null;
	} catch (IOException e) {
	    throw new OutputException("Could not close file: " + file.toURI(),
		    e);
	}
	try {
	    FileOutputStream headers = new FileOutputStream(headerFile);
	    addHeader(Integer.toString(getStatusCode()), getStatusMessage());
	    getHeaders().store(headers, "Headers");
	    headers.close();
	} catch (IOException e) {
	    throw new OutputException("Could write to file: "
		    + headerFile.toURI(), e);
	}
    }
}
