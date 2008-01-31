package net.webassembletool.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Map.Entry;

import net.webassembletool.ouput.Output;

/**
 * Resource implementation pointing to a file on the local FileSystem.
 * 
 * @author François-Xavier Bonnet
 * 
 */
public class FileResource implements Resource {
    private File file;
    private File headersFile;

    public FileResource(String url) {
	file = new File(url);
	headersFile = new File(url + ".headers");
    }

    public boolean exists() {
	return file.exists();
    }

    public void release() {
	file = null;
    }

    public void render(Output output) throws IOException {
	InputStream headersInputStream = new FileInputStream(headersFile);
	Properties headers = new Properties();
	headers.load(headersInputStream);
	headersInputStream.close();
	for (Iterator iterator = headers.entrySet().iterator(); iterator
		.hasNext();) {
	    Entry<String, String> header = (Entry<String, String>) iterator
		    .next();
	    if (header.getKey().equals("Charset"))
		output.setCharset(header.getValue());
	    else
		output.addHeader(header.getKey(), header.getValue());
	}
	InputStream inputStream = new FileInputStream(file);
	try {
	    output.open();
	    byte[] buffer = new byte[1024];
	    for (int len = -1; (len = inputStream.read(buffer)) != -1;) {
		output.write(buffer, 0, len);
	    }
	} finally {
	    inputStream.close();
	    output.close();
	}
    }
}
