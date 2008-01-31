package net.webassembletool.ouput;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Output implementation that saves the file and headers into two distinct
 * files.
 * 
 * @author François-Xavier Bonnet
 * 
 */
public class FileOutput implements Output {
    private final static Log log = LogFactory.getLog(FileOutput.class);
    private File file;
    private File headerFile;
    private FileOutputStream fileOutputStream;
    private Properties headers = new Properties();

    public FileOutput(String url) {
	file = new File(url);
	headerFile = new File(url + ".headers");
    }

    public void addHeader(String name, String value) {
	headers.put(name, value);
    }

    public void close() {
	try {
	    fileOutputStream.close();
	    fileOutputStream = null;
	    file = null;
	} catch (IOException e) {
	    log.fatal("Could not close file: " + file.toURI(), e);
	}
	FileOutputStream headersFileOutputStream;
	try {
	    headersFileOutputStream = new FileOutputStream(headerFile);
	    headers.store(headersFileOutputStream, "Headers");
	    headersFileOutputStream.close();
	} catch (IOException e) {
	    log.fatal("Could write to file: " + headerFile.toURI(), e);
	}
	headersFileOutputStream = null;
    }

    public void open() {
	try {
	    if (!file.exists()) {
		file.getParentFile().mkdirs();
		file.createNewFile();
	    }
	    fileOutputStream = new FileOutputStream(file);
	} catch (IOException e) {
	    log.fatal("Could not create file: " + file.toURI(), e);
	}
    }

    public void setCharset(String charset) {
	headers.put("Charset", charset);
    }

    public void write(byte[] bytes, int off, int len) throws IOException {
	fileOutputStream.write(bytes, off, len);
    }
}
