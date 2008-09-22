package net.webassembletool.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import net.webassembletool.ouput.Output;
import net.webassembletool.ouput.OutputException;

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

    /**
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(int i) throws IOException {
        fileOutputStream.write(i);
    }

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
	FileOutputStream headersFileOutputStream;
	try {
	    headersFileOutputStream = new FileOutputStream(headerFile);
	    addHeader(Integer.toString(getStatusCode()), getStatusMessage());
	    getHeaders().store(headersFileOutputStream, "Headers");
	    headersFileOutputStream.close();
	} catch (IOException e) {
	    throw new OutputException("Could write to file: "
		    + headerFile.toURI(), e);
	}
	headersFileOutputStream = null;
    }
}
