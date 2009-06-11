package net.webassembletool.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Map.Entry;

import net.webassembletool.RequestContext;
import net.webassembletool.output.Output;
import net.webassembletool.resource.Resource;
import net.webassembletool.resource.ResourceUtils;

/**
 * Resource implementation pointing to a file on the local FileSystem.
 * 
 * @author François-Xavier Bonnet
 * 
 */
public class FileResource extends Resource {
    private File file;
    private final File headersFile;

    public FileResource(String localBase, RequestContext target) {
	String url = ResourceUtils.getFileUrl(localBase, target);
	file = new File(url);
	headersFile = new File(url + ".headers");
    }

    @Override
    public void release() {
	file = null;
    }

    @Override
    public void render(Output output) throws IOException {
	InputStream headersInputStream = new FileInputStream(headersFile);
	Properties headers = new Properties();
	headers.load(headersInputStream);
	headersInputStream.close();
	Iterator<Entry<Object, Object>> iterator = headers.entrySet()
		.iterator();
	if (!iterator.hasNext())
	    throw new InvalidHeaderFileException("Invalid headers file");
	Entry<Object, Object> header = iterator.next();
	output.setStatus(Integer.parseInt(header.getKey().toString()), header
		.getValue().toString());
	while (iterator.hasNext()) {
	    header = iterator.next();
	    if (header.getKey().equals("Charset"))
		output.setCharsetName(header.getValue().toString());
	    else
		output.addHeader(header.getKey().toString(), header.getValue()
			.toString());
	}
	InputStream inputStream = new FileInputStream(file);
	try {
	    output.open();
	    byte[] buffer = new byte[1024];
	    OutputStream out = output.getOutputStream();
	    for (int len = -1; (len = inputStream.read(buffer)) != -1;) {
		out.write(buffer, 0, len);
	    }
	} finally {
	    inputStream.close();
	    output.close();
	}
    }

    /**
     * @see net.webassembletool.resource.Resource#getStatusCode()
     */
    @Override
    public int getStatusCode() {
	if (file.exists())
	    return 200;
	else
	    return 404;
    }
}
