package net.webassembletool.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import net.webassembletool.ResourceContext;
import net.webassembletool.output.Output;
import net.webassembletool.resource.Resource;
import net.webassembletool.resource.ResourceUtils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Resource implementation pointing to a file on the local FileSystem.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class FileResource extends Resource {
	private File file;
	private Map<String, String> headersMap;
	private int statusCode;
	private String statusMessage;

	public FileResource(String localBase, ResourceContext target)
			throws IOException {
		String url = ResourceUtils.getFileUrl(localBase, target);
		file = new File(url);
		File headersFile = new File(url + ".headers");
		if (file.exists() && headersFile.exists()) {
			headersMap = new HashMap<String, String>();
			InputStream headersInputStream = new FileInputStream(headersFile);
			Properties headers = new Properties();
			headers.load(headersInputStream);
			headersInputStream.close();

			Iterator<Entry<Object, Object>> iterator = headers.entrySet()
					.iterator();

			while (iterator.hasNext()) {
				Entry<Object, Object> header = iterator.next();
				if (StringUtils.isNumeric(header.getKey().toString())) {
					statusCode = Integer.parseInt(header.getKey().toString());
					statusMessage = header.getValue().toString();
				} else {
					headersMap.put(header.getKey().toString().toLowerCase(),
							header.getValue().toString());
				}
			}
		} else {
			statusCode = 404;
			statusMessage = "Not found";
		}
	}

	@Override
	public void release() {
		file = null;
	}

	@Override
	public void render(Output output) throws IOException {
		output.setStatus(statusCode, statusMessage);
		if (headersMap != null) {
			Iterator<Entry<String, String>> iterator = headersMap.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Entry<String, String> header = iterator.next();
				output.addHeader(header.getKey(), header.getValue());
			}
		}
		String charset = output.getHeader("content-encoding");
		if (charset != null) {
			output.setCharsetName(charset);
		}
		if (file != null) {
			InputStream inputStream = new FileInputStream(file);
			try {
				output.open();
				OutputStream out = output.getOutputStream();
				IOUtils.copy(inputStream, out);
			} finally {
				inputStream.close();
				output.close();
			}
		}
	}

	/**
	 * @see net.webassembletool.resource.Resource#getStatusCode()
	 */
	@Override
	public int getStatusCode() {
		return statusCode;
	}

	@Override
	public String getHeader(String name) {
		if (headersMap == null) {
			return null;
		}
		return headersMap.get(name);
	}
}
